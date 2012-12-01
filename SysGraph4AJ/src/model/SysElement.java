package model;

import java.util.HashSet;

public interface SysElement {

	/**gets the element name*/
	public String getName();

	/**gets the element owner*/
	public SysElement getOwner();

	/**sets the owner of this element*/
	public void setOwner(SysElement e);

	/**gets the fully qualified name*/
	public String getFullyQualifiedName();

	/**returns true if this element represents the same element of this instance*/
	public boolean equals(SysElement e);

	/**returns a partial clone of this element, usually just name and owner*/
	public SysElement partialClone();

	/**gets all the child elements of this element. It may return an empty hash if there are no child element,
	 * even for this element can not hold a child or it hasn't one*/
	public HashSet<SysElement> getChildElements();

	/**gets the required element. 
	 * @param thisName should be just a single name, not a fully qualified name*/
	public SysElement get(String thisName, String sig, boolean isLast);

	/**@return the maximum element given the fully qualified name*/
	public SysElement getMax(String called, String sig);

	/**add and element to this element if possible*/
	public void add(SysElement e);
	
	/**@return String a string that represents all the details of this element*/
	public String viewState();
}
