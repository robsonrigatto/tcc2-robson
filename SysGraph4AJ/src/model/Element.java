package model;

import java.util.HashSet;

public interface Element {

	/**gets the element name*/
	public String getName();

	/**gets the element owner*/
	public Element getOwner();

	/**sets the owner of this element*/
	public void setOwner(Element e);

	/**gets the fully qualified name*/
	public String getFullyQualifiedName();

	/**returns true if this element represents the same element of this instance*/
	public boolean equals(Element e);

	/**returns a partial clone of this element, usually just name and owner*/
	public Element partialClone();

	/**gets all the child elements of this element. It may return an empty hash if there are no child element,
	 * even for this element can not hold a child or it hasn't one*/
	public HashSet<Element> getChildElements();

	/**gets the required element. 
	 * @param thisName should be just a single name, not a fully qualified name*/
	public Element get(String thisName, String sig, boolean isLast);

	/**@return the maximum element given the fully qualified name*/
	public Element getMax(String called, String sig);

	/**add and element to this element if possible*/
	public void add(Element e);
	
	/**@return String a string that represents all the details of this element*/
	public String viewState();
}
