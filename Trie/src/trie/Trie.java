package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
        Indexes temp = new Indexes(0, (short) 0, (short) (allWords[0].length() - 1));
        root.firstChild = new TrieNode(temp, null, null);
        for (int i = 1; i < allWords.length; i++) {
            insertNode(allWords, root, root.firstChild, i, 0);
        }
        return root;
	}
    private static void insertNode(String[] allWords, TrieNode parent, TrieNode ptr, int index, int charIndex) {
        String word = allWords[index];
        String ptrWord = allWords[ptr.substr.wordIndex];

        boolean newCommonChars = false;

        while (charIndex <= ptr.substr.endIndex && charIndex < word.length() && ptrWord.charAt(charIndex) == word.charAt(charIndex)) {
            newCommonChars = true;
            charIndex++;
        }

        if (!newCommonChars) {
            if (ptr.sibling == null) {
                Indexes tempIndex = new Indexes(index, (short) charIndex, (short) (word.length() - 1));
                ptr.sibling = new TrieNode(tempIndex, null, null);
            } else {
                insertNode(allWords, parent, ptr.sibling, index, charIndex);
            }
        } else {
            if ((charIndex - 1 < ptr.substr.endIndex) && ptr.firstChild != null) {
                Indexes interIdx = new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, (short) (charIndex - 1));
                TrieNode interNode = new TrieNode(interIdx, ptr, ptr.sibling);
                ptr.substr.startIndex = (short) (interNode.substr.endIndex + 1);

                if (parent.firstChild == ptr) {
                    parent.firstChild = interNode;
                } else {
                    TrieNode tempPtr = parent.firstChild;
                    while (tempPtr.sibling != ptr && tempPtr.sibling != null) {
                        tempPtr = tempPtr.sibling;
                    }
                    tempPtr.sibling = interNode;
                }
                Indexes wordToAddIndex = new Indexes(index, (short) charIndex, (short) (word.length() - 1));
                ptr.sibling = new TrieNode(wordToAddIndex, null, null);
                return;
            }

            if (ptr.firstChild != null) {
                insertNode(allWords, ptr, ptr.firstChild, index, charIndex);
            } else {
                short temp = ptr.substr.endIndex;
                ptr.substr.endIndex = (short) (charIndex - 1);
                Indexes oldIndex = new Indexes(ptr.substr.wordIndex, (short) charIndex, temp);
                short endIndexNewWord = (short) (word.length() - 1);
                Indexes newIndex = new Indexes(index, (short) charIndex, endIndexNewWord);
                TrieNode child1 = new TrieNode(oldIndex, null, null);
                child1.sibling = new TrieNode(newIndex, null, null);
                ptr.firstChild = child1;
            }
        }
    }
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		TrieNode ptr = root.firstChild;
        ArrayList<TrieNode> list = new ArrayList<TrieNode>();
        completion(ptr, allWords, prefix, list);
        return list.isEmpty() ? null : list;
	}
	
	private static void completion(TrieNode ptr, String[] allWords, String prefix, ArrayList<TrieNode> list) {
        if (!prefix.isEmpty()) {
            String currentWord = allWords[ptr.substr.wordIndex].substring(ptr.substr.startIndex, ptr.substr.endIndex + 1);
            if (currentWord.startsWith(prefix) || prefix.startsWith(currentWord)) {
                addFromSubtree(ptr, allWords, list, prefix);
            } else if (ptr.sibling != null) {
                completion(ptr.sibling, allWords, prefix, list);
            }
        }
    }
	
	private static void addFromSubtree(TrieNode ptr, String[] allWords, ArrayList<TrieNode> list, String prefix) {
        if (ptr.firstChild == null && allWords[ptr.substr.wordIndex].startsWith(prefix)) {
            list.add(ptr);
        }
        if (ptr.firstChild != null) {
            addFromSubtree(ptr.firstChild, allWords, list, prefix);
        }
        if (ptr.sibling != null) {
            addFromSubtree(ptr.sibling, allWords, list, prefix);
        }
    }
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
