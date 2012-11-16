import java.io.*;
import java.util.*;
public class Useful
{
	
	/* Constructorul clasei Useful*/
	public Useful(Collection<Node> bookCollection, Node root)
	{
		Object[] obook = bookCollection.toArray();
		int N = 0;   
		int Cash = root.getAttribute("cash");
		int[] useful = new int[obook.length + 1];
		int[] price = new int[obook.length + 1];
		for (int n = 0; n < obook.length; n++)
		{	
			if ((((Node)obook[n]).getAttribute("usefulness") > -1)
					&&(((Node)obook[n]).getAttribute("price") > -1)
						&&(((Node)obook[n]).getAttribute("isbn") > -1))
			{
				N++;
		    	useful[N] = ((Node)obook[n]).getAttribute("usefulness");
		    	price[N] = ((Node)obook[n]).getAttribute("price");
			}
		}

		int[][] opt = new int[N+1][Cash+1];
		boolean[][] sol = new boolean[N+1][Cash+1];

		
		for (int n = 1; n <= N; n++) 
		{
		    for (int w = 1; w <= Cash; w++) 
		    {

		        // don't buy item n
		        int book1 = opt[n-1][w];

		        // buy item n
		        int book2 = Integer.MIN_VALUE;
		        if (price[n] <= w)
		        	book2 = useful[n] + opt[n-1][w-price[n]];

		        // select better of two books
		        opt[n][w] = Math.max(book1, book2);
		        sol[n][w] = (book2 > book1);
		    }
		}

		// determine which books to buy

		boolean[] buy = new boolean[N+1];
		int q = 0;
		for (int n = N, w = Cash; n > 0; n--)
	   	{
		    if (sol[n][w])
		   	{ 
				buy[n] = true;
			  	w = w - price[n];
				q++;
			}
		    else 
				buy[n] = false;
		}
		int[] isbn_vec = new int[q];
		
		
		for (int n = N, j = 0; n>0; n--)
		{
			if (buy[n])
				isbn_vec[j++] = ((Node)obook[n-1]).getAttribute("isbn");
		}

		Arrays.sort(isbn_vec);
		for (int i = 0; i < q; i++)
			System.out.println(isbn_vec[i]);
		
	}
}
