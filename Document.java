/*---------- Document.java ----------*/
import java.io.*;
import java.util.*;
public class Document
{
/**/
	public static final String specialDelim = "><\"/ \t\n\r=";
	private Node root;
	private Document(Node p_root)
	{
		root = p_root;
	}
	public Node getRoot()
	{
		return root;
	}
	public static Document parse(BufferedReader in) throws IOException, parseException
	{
		Node node = readNode(null, in);
		if (node == null)
			return null;
		Document doc = new Document(node);
		return doc;
	}
	
/**
*  Moves the buffer at the last read whitespace 
*/
	
	public static void ignoreWhiteSpace(BufferedReader in) 
			throws IOException, parseException
	{
		do { 
			in.mark(1);
			int c = in.read();
			if ((c == -1) || (!Character.isWhitespace(c)))
			{
				in.reset();
				break;
			}
		} while(true);
	}

/**/	
	public static boolean isDelim(char c)
	{
		return (specialDelim.indexOf(c) != -1);
	}

	/**
	 * Read a char and throw an exception on EOF
	 * @param in Input buffer - will be moved with one position. 
	 * 			 If EOF is found, an exception is thrown.
	 * @return the character read from the input buffer
	 * @throws IOException, parseException - If an I/O error occurs.
	 * 
	 */

	private static char readChar(BufferedReader in) throws IOException, parseException
	{
		int chint = in.read();
		if (-1 == chint)
			throw new parseException("unexpected eof");
		return (char) chint;
	}


	public static String readIdentifier(BufferedReader in)
	 		throws IOException, parseException
	{
		char c;
		String ret = "";
		do {
			in.mark(1);
			c = readChar(in);
			if (isDelim(c))
			{
				in.reset();
				break;
			}
			else
			{
				ret += c;
			}
		} while( true );
	   	return ret;
	}

	
	public static String readQuotedValue(BufferedReader in)
		throws IOException, parseException
	{
		String val = "";
		char c = readChar(in);
		if ('"' != c)
			throw new parseException("Not a valid VALUE. Must begin with \"");
		do 
		{
			c = readChar(in);
			if ('"' == c)
				break;
			val += c;
		} while(true);
		return val;
	}

/**/	
	public static void readAttribute (Node node, BufferedReader in)
			throws IOException, parseException
	{
		String key, val = "";
		key = readIdentifier(in);
		ignoreWhiteSpace(in);
		char c = readChar(in);
		if ('=' != c) 
			throw new parseException("expecting a value for attribute " +
								key + " in node " + node.getName());
		// we read "key ="
		ignoreWhiteSpace(in);
		val = readQuotedValue(in);
		node.attribute.put(key, val);
	}

/**/
	public static boolean readExpected(BufferedReader in, char c)
		throws IOException, parseException
	{
		if (peek(in) != c)
			throw new parseException("Did not read " +c);
		return true;
	}


	/**
	 * a blob is terminated by a '<'.
	 * a blob must not contain a '>' or a '<'.
	 */
	public static String tryReadBlob(BufferedReader in) throws IOException, parseException
	{
		in.mark(100);
		// we don't know if it's a blob.
		// try to find a '<'.
		ignoreWhiteSpace(in);
		char c = readChar(in);
		
		// it's not a blob.
		if (c == '<') {
			in.reset();
			// let other functions decompose tag structure.
			return null;
		}

		// it's a blob
		// reset so that we keep whitespace in the blob text.
		in.reset();
		
		// read all until the endtag begins
		String blob = "";
		in.mark(1);
		c = readChar(in);
		while( c != '<') {
			blob += c;
			in.mark(1);
			c = readChar(in);
		}
		in.reset();
		return blob;

	}

	/* input format: </ name >	*/
	public static boolean tryReadEndTag(String name, BufferedReader in) throws IOException, parseException
	{
		ignoreWhiteSpace(in);

		in.mark(2);
		char c1 = readChar(in);
		char c2 = readChar(in);

		// an end tag must begin with '</'
		if ((c1 != '<') || (c2 != '/'))
		{
			in.reset();
			return false;
		}

		ignoreWhiteSpace(in);

		String foundName = readIdentifier(in);
		// end tag name must be equal to the last opened tag name
		if (!(foundName.equals(name)))
		{
			in.reset();
			return false;
		}

		ignoreWhiteSpace(in);
		
		// an end tag must be closed by a '>'
		if (readChar(in) != '>')
			return false;
		return true;
	}

	public static void readEndTag(String name, BufferedReader in) throws IOException, parseException
	{
		if (!(tryReadEndTag(name, in)))
				throw new parseException("Invalid endtag at node: " + name);
		return;
	}

/**/
	public static void readChildrenAndEndTag (Node node, BufferedReader in)
			throws IOException, parseException
	{
		String allblob = ""; // accumulator for all text inside this tag.
		
		// some childern may follow, but an endtag is mandatory.
		do
		{
			// if any blob was found, add it to the accumulator
			String blob = tryReadBlob(in);
			if (blob != null)
				allblob += blob;

			
			ignoreWhiteSpace(in);
			if (true == tryReadEndTag(node.getName(), in))
			{
				// we succeeded in reading an endtag.
				// all other nodes cannot be children of 'node'
				node.text = allblob;
				return;
			}		
			// because tryReadEndTag failed, we must have a child node here.
			Node fiu = readNode(node, in);
			node.addFiu(fiu);
		} while(true);
	}

	public static char peek(BufferedReader in) throws IOException, parseException
	{
		in.mark(1);
		char ret = readChar(in);
		in.reset();
		return ret;
	}


	/**
	 * read " < name "
	 */
	public static Node readStartTag(Node parent, BufferedReader in) throws IOException, parseException
	{
		Node node;
		String name = "";
		ignoreWhiteSpace(in);
		
		char c = readChar(in);
		if (c != '<')
			throw new parseException("Expected a '<' before " + c);

		ignoreWhiteSpace(in);
		
		if (isDelim(peek(in)))
				throw new parseException("invalid xml, unexpected " +
							c + " while reading tag identifier");
		
		name = readIdentifier(in);
		node = new Node(name, parent);
		return node;
	}


	/**
	 * expected input:
	 * <   nodeName  [ attribute = "val" ]* >
	 * 	  ...?
	 * </  nodename >
	 *
	 * OR
	 * <  nodename [ attribute = "val" ]* />
	 */
	public static Node readNode(Node parent, BufferedReader in)
			throws IOException, parseException
	{
		Node node = readStartTag(parent, in);
		boolean node_complete = false;
		do {
			ignoreWhiteSpace(in);
			char c;
			switch(c = peek(in))
			{
				case '>':
					// actually read the last peeked char (which is == '>').
					c = readChar(in);
					readChildrenAndEndTag(node, in);
					return node;
				case '/':
					// actually read the last peeked char (which is == '/').
					readChar(in);
					// an '/' must be immediatly followed by an '>'
					if (readChar(in) != '>')
						throw new parseException("invalid xml ending." +
							 	"Found '/' not followed by an '>'");
					// found "/>" type of tag.
					// these tags have no children, so we just return.
					node_complete = true;
					break;
				case '<':
				case '=':
				case '"':
					throw new parseException("unexpected char " + c);
				default:
					readAttribute(node, in);
					break;
			}
		} while(!node_complete);
		return node;
	}
} //~ end class def.
