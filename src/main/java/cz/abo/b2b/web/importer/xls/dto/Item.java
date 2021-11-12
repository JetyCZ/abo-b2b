package cz.abo.b2b.web.importer.xls.dto;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Tomas Kodym
 */

public class Item implements Serializable, Comparable<Item>
{
    public Integer parsedIdx;
    private boolean bio;
    private Integer itemId;
    private String itemName;
    private Double itemQuantity;
    private Double itemPrice;
    private Integer itemTax;
    private Integer rowNum;
    public String description;

    public Item() {
    }

    public Item(String itemName, Double itemQuantity, Double itemPrice, Integer itemTax)
    {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        setItemPrice(itemPrice);
        this.itemTax = itemTax;
    }

    public Integer getItemId()
    {
        return itemId;
    }

    public void setItemId(Integer itemId)
    {
        this.itemId = itemId;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public Double getItemQuantity()
    {
        return itemQuantity;
    }

    public void setItemQuantity(Double itemQuantity)
    {
        this.itemQuantity = itemQuantity;
    }

    public Double getItemPrice()
    {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice)
    {
        this.itemPrice = round(itemPrice,5);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Integer getItemTax()
    {
        return itemTax;
    }

    public void setItemTax(Integer itemTax)
    {
        this.itemTax = itemTax;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemId.equals(item.itemId) &&
                itemName.equals(item.itemName) &&
                itemQuantity.equals(item.itemQuantity) &&
                itemPrice.equals(item.itemPrice) &&
                itemTax.equals(item.itemTax);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(itemId, itemName, itemQuantity, itemPrice, itemTax);
    }

    @Override
    public String toString()
    {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemQuantity='" + itemQuantity + '\'' +
                ", itemPrice=" + itemPrice +
                ", itemTax=" + itemTax +
                '}';
    }

    /**
     * Excel row 1 = rowNum 0
     */
    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    @Override
    public int compareTo(Item o) {
        return o.getRowNum().compareTo(this.getRowNum());
    }

    public boolean isBio() {
        return bio;
    }

    public void setBio(boolean bio) {
        this.bio = bio;
    }

    public Product toProduct(Supplier saved)  {
        double VAT = 0.01 * itemTax;
        BigDecimal priceVAT = new BigDecimal((1 + VAT) * itemPrice * 1000);
        BigDecimal quantity = new BigDecimal(itemQuantity);
        if (quantity.intValueExact() != 1) {
            quantity = quantity.divide(new BigDecimal(1000));
        }
        Product product = new Product(itemName, priceVAT, VAT, description, quantity, saved);
        product.setRowNum(rowNum);
        return product;
    }
}