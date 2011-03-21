package models;

import java.sql.Timestamp;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Customer extends Model {
    public String name;
    public String address;
    public String nip;
    public String phone;
    public String email;
    public String web;
    public String notes;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
