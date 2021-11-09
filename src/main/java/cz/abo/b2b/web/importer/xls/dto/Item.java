package cz.abo.b2b.web.importer.xls.dto;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Tomas Kodym
 */

@Entity(name = "Item")
@Table(name = "item")
@Transactional
public class Item implements Serializable, Comparable<Item>
{
    @Column
    public Integer parsedIdx;

    @Column
    private boolean bio;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)

    private Integer itemId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_quantity", nullable = false, length = 50)
    private Double itemQuantity;

    @Column(name = "item_price", nullable = false)
    private Double itemPrice;

    @Column(name = "item_tax", nullable = false)
    private Integer itemTax;

    @Column
    private Integer rowNum;

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
}