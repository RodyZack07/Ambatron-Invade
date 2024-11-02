package com.example.savesthekunti.Model;

public class Skin {
    private String id_skin;
    private String nama_skin;
    private boolean status_terkunci;
    private String created_at;
    private String updated_at;

    public Skin() {
        // Constructor kosong diperlukan untuk Firebase
    }

    public Skin(String id_skin, String nama_skin, boolean status_terkunci, String created_at, String updated_at) {
        this.id_skin = id_skin;
        this.nama_skin = nama_skin;
        this.status_terkunci = status_terkunci;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters dan Setters
    public String getId_skin() {
        return id_skin;
    }

    public void setId_skin(String id_skin) {
        this.id_skin = id_skin;
    }

    public String getNama_skin() {
        return nama_skin;
    }

    public void setNama_skin(String nama_skin) {
        this.nama_skin = nama_skin;
    }

    public boolean isStatus_terkunci() {
        return status_terkunci;
    }

    public void setStatus_terkunci(boolean status_terkunci) {
        this.status_terkunci = status_terkunci;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
