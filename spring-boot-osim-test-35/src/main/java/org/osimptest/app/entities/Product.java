package org.osimptest.app.entities;

/*
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/* */
///*

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ts_products")
public class Product {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "product_id")
    UUID id;

    String type;

//    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "parentProduct")
    List<Product> subproducts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_product_id", referencedColumnName = "product_id")
    Product parentProduct;

    String name;

    int price;

    UUID externalProductId;

    //    /*
    @Version
    Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    /**/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Product> getSubproducts() {
        return subproducts;
    }

    public void setSubproducts(List<Product> subproducts) {
        this.subproducts = subproducts;
    }

    public Product getParentProduct() {
        return parentProduct;
    }

    public void setParentProduct(Product parentProduct) {
        this.parentProduct = parentProduct;
    }

    public UUID getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(UUID externalProductId) {
        this.externalProductId = externalProductId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
