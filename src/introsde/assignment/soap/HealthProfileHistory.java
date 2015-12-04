
package introsde.assignment.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for healthProfileHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="healthProfileHistory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="healthProfile_history" type="{http://soap.assignment.introsde/}measure" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "healthProfileHistory", propOrder = {
    "healthProfileHistory"
})
public class HealthProfileHistory {

    @XmlElement(name = "healthProfile_history", nillable = true)
    protected List<Measure> healthProfileHistory;

    /**
     * Gets the value of the healthProfileHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the healthProfileHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHealthProfileHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Measure }
     * 
     * 
     */
    public List<Measure> getHealthProfileHistory() {
        if (healthProfileHistory == null) {
            healthProfileHistory = new ArrayList<Measure>();
        }
        return this.healthProfileHistory;
    }

}
