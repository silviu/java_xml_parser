/*----------- Node.java ----------*/
import java.io.*;
import java.util.*;
public class Node 
{
    String nume;
    Node parinte;
    ArrayList<Node> fii = new ArrayList<Node>();
	HashMap<String, String> attribute = new HashMap<String, String>();	
    String text;

	/**
	 * make a new Node and set it's parrent and name.
	 * @param p_nume name of the node ( <name /> or <name> childern, text </name>
	 * @param p_parinte reference to parent node (null for root node).
	 */
	public Node(String p_nume, Node p_parinte)
	{
		nume = p_nume;
		parinte = p_parinte;
	}
	
	public Collection<String> getAttributes()
	{
		return attribute.keySet();
	}

	/**
	 * Returns a collection with all child nodes.
	 * @return returns a shallow copy (the elements themselves are not copied.)
	 * 			to protect the .fii member from being altered by the caller.
	 */
        @SuppressWarnings( "unchecked" )
	public Collection<Node> getChildren()
	{
	    return ((Collection<Node>)(fii.clone()));
	}
	public Node getParent()
	{
		return parinte;
	}	
	/**
	 */
	public int getAttribute(String searchedKey)
	{
		if (attribute.containsKey(searchedKey))
			return Integer.parseInt(attribute.get(searchedKey));
		return -1;
	}

	public void setAttribute(String searchedKey, String val) throws Exception 
	{
		if (attribute.containsKey(searchedKey))
			throw new Exception("atributul apare de doua ori in tagul " + nume);
		attribute.put(searchedKey, val);
	}

	public String getName()
	{
		return nume;
	}
	public void addFiu(Node p_fiu)
	{
		fii.add(p_fiu);
	}
	public void remFiu()
	{
	}

	public String attributeToString(String prefixUnit)
	{
		String ret = "";
		for(String key : attribute.keySet())
		{
			ret += "\n";
			String value = attribute.get(key);
			ret += prefixUnit + key + " = \""+ value + "\"";
		}
		return ret;
	}
	private String toString (String prefixUnit, int level)
	{
		String prefix = "";
		for (int i = 0; i < level; i ++)
			prefix += prefixUnit;
		String ret = "";
		ret += prefix + "<" + getName() + " " + attributeToString(prefix+prefix) + ">\n";
		for (Node fiu : fii) 
			ret += fiu.toString(prefixUnit, level + 1) + "\n";
		ret += prefix + text + "\n";
		ret += prefix + "</" + getName() + ">\n";
		return ret;
	}
	public String toString()
	{
		return toString("    ", 0);
	}
}	
