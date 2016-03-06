package org.gestern.gringotts;

import com.avaje.ebean.EbeanServer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.api.impl.VaultConnector;
import org.gestern.gringotts.data.DAO;
import org.gestern.gringotts.data.DerbyDAO;
import org.gestern.gringotts.data.EBeanDAO;
import org.gestern.gringotts.data.Migration;
import org.gestern.gringotts.event.AccountListener;
import org.gestern.gringotts.event.PlayerVaultListener;
import org.gestern.gringotts.event.VaultCreator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.gestern.gringotts.Configuration.CONF;
import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.dependency.Dependency.DEP;


public class Gringotts extends JavaPlugin {

    /** The Gringotts plugin instance. */
    public static Gringotts G;

    public DAO dao;

    private Logger log;

    private Commands gcommand;

    /** Manages accounts. */
    public Accounting accounting;

    /** 
     * The account holder factory is the place to go if you need an AccountHolder instance for an id.
     */
    public final AccountHolderFactory accountHolderFactory = new AccountHolderFactory();

    @Override
    public void onEnable() {

        G = this;

        try {
            log = getLogger();

            // just call DAO once to ensure it's loaded before startup is complete
            dao = getDAO();

            // load and init configuration
            saveDefaultConfig(); // saves default configuration if no config.yml exists yet
            reloadConfig();

            gcommand = new Commands(this);

            accounting = new Accounting();

            registerCommands();
            registerEvents();
            registerEconomy();

        } catch(GringottsStorageException | GringottsConfigurationException e) {
            log.severe(e.getMessage()); 
            this.disable();
        } catch (RuntimeException e) {
            this.disable();
            throw e;
        }


        log.fine("enabled");
    }

    private void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
        log.warning("Gringotts disabled due to startup errors.");
    }

    @Override
    public void onDisable() {

        // shut down db connection
        try{
            if (dao !=null) dao.shutdown();
        } catch (GringottsStorageException e) {
            log.severe(e.toString()); 
        }

        log.info("disabled");
    }

    private void registerCommands() {
        CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor moneyAdminCommands = gcommand.new Moneyadmin();
        CommandExecutor adminCommands = gcommand.new GringottsCmd();

        getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(moneyAdminCommands);
        getCommand("gringotts").setExecutor(adminCommands);
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new AccountListener(), this);
        manager.registerEvents(new PlayerVaultListener(), this);
        manager.registerEvents(new VaultCreator(), this);

        // listeners for other account types are loaded with dependencies
    }


    /**
     * Register Gringotts as economy provider for vault.
     */
    private void registerEconomy() {
        if (DEP.vault.exists()) {
            final ServicesManager sm = getServer().getServicesManager();
            sm.register(Economy.class, new VaultConnector(), this, ServicePriority.Highest);
            log.info("Registered Vault interface.");
        } else {
            log.info("Vault not found. Other plugins may not be able to access Gringotts accounts.");
        }
    }

    /**
     * Register an accountholder provider with Gringotts. This is necessary for Gringotts to find and create account holders
     * of any non-player type. Registering a provider for the same type twice will overwrite the previously registered provider.
     * @param type type id for an account type
     * @param provider provider for the account type
     */
    public void registerAccountHolderProvider(String type, AccountHolderProvider provider) {
        accountHolderFactory.registerAccountHolderProvider(type, provider);
    }

    /**
     * Get the configured player interaction messages.
     * @return the configured player interaction messages
     */
    public FileConfiguration getMessages() {

        String langPath = "i18n/messages_" + CONF.language + ".yml";

        // try configured language first
        InputStream langStream = getResource(langPath);
        final FileConfiguration conf;
        if (langStream != null) {
            Reader langReader = new InputStreamReader(getResource(langPath), Charset.forName("UTF-8"));
            conf = YamlConfiguration.loadConfiguration(langReader);
        } else {
            // use custom/default
            File langFile = new File(getDataFolder(), "messages.yml");
            conf = YamlConfiguration.loadConfiguration(langFile);
        }

        return conf;
    }

    // override to handle custom config logic and language loading
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        CONF.readConfig(getConfig());
        LANG.readLanguage(getMessages());
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        File defaultMessages = new File(getDataFolder(), "messages.yml");
        if (! defaultMessages.exists()) saveResource("messages.yml", false);
    }

    private DAO getDAO() {

        setupEBean();

        // legacy support: migrate derby if it hasn't happened yet
        // automatically migrate derby to eBeans if db exists and migration flag hasn't been set
        Migration migration = new Migration();

        DerbyDAO derbyDAO;
        if (!migration.isDerbyMigrated() &&
                (derbyDAO = DerbyDAO.getDao()) != null) {
            log.info("Derby database detected. Migrating to Bukkit-supported database ...");
            EBeanDAO eBeanDAO = EBeanDAO.getDao();
            migration.doDerbyMigration(derbyDAO, eBeanDAO);
        }

        if (!migration.isUUIDMigrated()) {
            log.info("Player database not migrated to UUIDs yet. Attempting migration");
            migration.doUUIDMigration();
        }

        return EBeanDAO.getDao();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return EBeanDAO.getDatabaseClasses();
    }

    /**
     * Some awkward ritual that Bukkit requires to initialize all the DB classes.
     * Does nothing if they have already been set up.
     */
    private void setupEBean() {
        try {
            EbeanServer db = getDatabase();
            for (Class<?> c : getDatabaseClasses())
                db.find(c).findRowCount();
        } catch (Exception ignored) {
            log.info("Initializing database tables.");
            installDDL();
        }
    }

}
