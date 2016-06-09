package org.gestern.gringotts;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.gestern.gringotts.currency.GringottsCurrency;

public class Util {
    
    private Util() {}

    /**
     * Check whether a block is a sign or wall sign type.
     * @param block block to check
     * @return true if the block is a sign or wall sign
     */
    public static boolean isSignBlock(Block block) {
        return block.getState() instanceof Sign;
    }

    /**
     * Compares whether a version string in standard format (dotted decimals) is greater than another.
     * @param version version string to check
     * @param atLeast minimum expected version
     * @return true if version is greater than greaterThanVersion, false otherwise
     */
    public static boolean versionAtLeast(String version, String atLeast) {

        int[] versionParts = versionParts(version);
        int[] atLeastParts = versionParts(atLeast);

        for (int i=0; i<versionParts.length && i<atLeastParts.length; i++) {
            // if any more major version part is larger, our version is newer
            if (versionParts[i] > atLeastParts[i])
                return true;
            else if (versionParts[i] < atLeastParts[i])
                return false;
        }

        // the at least version has more digits
        if (atLeastParts.length > versionParts.length)
            return false;

        // supposedly the versions are equal
        return true;
    }

    /**
     * Break a version string into parts.
     * @param version version string to handle
     * @return array with dotted decimal strings turned into int values
     */
    public static int[] versionParts(String version) {
        String[] strparts = version.split("\\.");
        int[] parts = new int[strparts.length];

        for (int i=0; i<strparts.length; i++) {
            // just cut off any non-number part
            String number = strparts[i].replaceAll("(\\d+).*","$1");
            int part = 0;
            try { part = Integer.parseInt(number); }
            catch(NumberFormatException ignored) {}

            parts[i] = part;
        }

        return parts;
    }

    /**
     * Get a formatted currency value. The value display includes the currency name.
     * @param value the value in cents
     * @return formatted currency value
     */
    public static String format(double value) {
        GringottsCurrency cur = Configuration.CONF.currency;
        String formatString = "%."+cur.digits+"f %s";
        return String.format(formatString, value, value==1.0? cur.name : cur.namePlural);
    }

    /**
     * Find a valid container block for a given sign, if it exists.
     * @param sign sign to check
     * @return container for the sign if available, null otherwise.
     */
    public static Block chestBlock(Sign sign) {
        // is sign attached to a valid vault container?
        Block signBlock = sign.getBlock();
        org.bukkit.material.Sign signData = (org.bukkit.material.Sign)signBlock.getState().getData();
        BlockFace attached = signData.getAttachedFace();

        // allow either the block sign is attached to or the block below the sign as chest block. Prefer attached block.
        Block blockAttached = signBlock.getRelative(attached);
        Block blockBelow = signBlock.getRelative(BlockFace.DOWN);
        if (validContainer(blockAttached.getType()))
            return blockAttached;
        if (validContainer(blockBelow.getType()))
            return blockBelow;

        return null; // no valid container
    }

    /**
     * Return whether the given material is a valid container type for Gringotts vaults.
     * @param material material to check
     * @return whether the given material is a valid container type for Gringotts vaults
     */
    public static boolean validContainer(Material material) {
        switch (material) {
            case CHEST:
            case TRAPPED_CHEST:
            case DISPENSER:
            case FURNACE:
            case HOPPER:
            case DROPPER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Alias for color code translation. Uses '&' as code prefix.
     * @param s String to translate color codes.
     * @return the translated String
     */
    public static String translateColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}