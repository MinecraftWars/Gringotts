package org.gestern.gringotts.data;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "gringotts_account")
@UniqueConstraint(columnNames = {"type", "owner"})
public class EBeanAccount {
    @Id
    int    id;
    /**
     * Type string.
     */
    @NotNull
    String type;
    /**
     * Owner id.
     */
    @NotNull
    String owner;
    /**
     * Virtual balance.
     */
    @NotNull
    long   cents;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getCents() {
        return cents;
    }

    public void setCents(long cents) {
        this.cents = cents;
    }

    @Override
    public String toString() {
        return "EBeanAccount(" + owner + "," + type + ": " + cents + ")";
    }

}