package com.tpximpact.urlshortener.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "urls", uniqueConstraints = @UniqueConstraint(columnNames = "alias"))
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false, length = 2048)
    private String fullUrl;

    public Url() {}

    public Url(String fullUrl, String alias) {
        this.fullUrl = fullUrl;
        this.alias = alias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getFullUrl() { return fullUrl; }
    public void setFullUrl(String fullUrl) { this.fullUrl = fullUrl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Url url)) return false;
        return Objects.equals(alias, url.alias) && Objects.equals(fullUrl, url.fullUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullUrl, alias);
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", alias='" + alias + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                '}';
    }
}
