package com.example.test.model;

public class Repo {
    public final String name;
    public final String full_name;
    public final String language;
    public final String created_at;
    public final String updated_at;

    public Repo(String name, String full_name, String language, String created_at, String updated_at) {
        this.name = name;
        this.full_name = full_name;
        this.language = language;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ").append(name).append("\n");
        sb.append("full_name: ").append(full_name).append("\n");
        sb.append("language: ").append(language).append("\n");
        sb.append("created_at: ").append(created_at).append("\n");
        sb.append("updated_at: ").append(updated_at).append("\n");
        sb.append("\n");
        return sb.toString();
    }
}
