//package edu.iastate.cs228.hw3;

/**
 * @author Xuan Lu
 */

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implementation of the list interface based on linked nodes that store
 * multiple items per node. Rules for adding and removing elements ensure that
 * each node (except possibly the last one) is at least half full.
 *
 * A link to the JavaDoc documentation for the interface AbstractSequentialList
 * <E> is provided next to the pdf spec on Blackboard.
 *
 * You should carefully study the complete methods given below to learn how to
 * go about implementing other methods.
 *
 * You are encouraged to introduce private methods that can be used to simplify
 * the implementation of public methods.
 */
public class ChunkyList<E extends Comparable<? super E>> extends AbstractSequentialList<E> {
	/**
	 * Default number of elements that may be stored in each node.
	 */
	private static final int DEFAULT_NODESIZE = 4;

	/**
	 * Number of elements that can be stored in each node.
	 */
	private final int nodeSize;

	/**
	 * Dummy node for head. It should be private but set to public here only for
	 * grading purpose. In practice, you should always make the head of a linked
	 * list a private instance variable.
	 */
	private Node head;

	/**
	 * Dummy node for tail.
	 */
	private Node tail;

	/**
	 * Number of elements in the list.
	 */
	private int size;

	/**
	 * Constructs an empty list with the default node size.
	 */
	public ChunkyList() {
		this(DEFAULT_NODESIZE);
	}

	/**
	 * Constructs an empty list with the given node size.
	 * 
	 * @param nodeSize
	 *            number of elements that may be stored in each node, must be an
	 *            even number
	 */
	public ChunkyList(int nodeSize) {
		if (nodeSize <= 0 || nodeSize % 2 != 0)
			throw new IllegalArgumentException();

		// dummy nodes
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		this.nodeSize = nodeSize;
	}

	/**
	 * Constructor for grading only. Fully implemented.
	 * 
	 * @param head
	 * @param tail
	 * @param nodeSize
	 * @param size
	 */
	public ChunkyList(Node head, Node tail, int nodeSize, int size) {
		this.head = head;
		this.tail = tail;
		this.nodeSize = nodeSize;
		this.size = size;
	}

	/**
	 * @return the size of the list
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * 
	 * @param item
	 *            item to be added
	 * @return true if added successfully, false otherwise
	 */
	@Override
	public boolean add(E item) {
		if (item == null)
			throw new NullPointerException();
		// If there is no node in the list.
		if (this.size == 0) {
			Node toAdd = new Node();
			toAdd.addItem(item);
			this.head.next = toAdd;
			this.tail.previous = toAdd;
			toAdd.previous = this.head;
			toAdd.next = this.tail;
			size += 1;
			return true;
		}
		// If there one or more node in the list
		if (this.size > 0) {
			Node toAdd = this.tail.previous;
			if (toAdd.count < this.nodeSize) {
				toAdd.addItem(item);
				this.size += 1;
				return true;
			}
			// If current node is full, create a new node and change the link
			if (toAdd.count == this.nodeSize) {
				Node newNode = new Node();
				newNode.addItem(item);
				// Insert the new now to the end of the list(Before the tail
				// node)
				newNode.next = this.tail;
				newNode.previous = toAdd;
				toAdd.next = newNode;
				this.tail.previous = newNode;
				this.size += 1;
				return true;
			}

		}

		return false;
	}

	/**
	 * add the item in the specified the position
	 * 
	 * @param pos
	 *            the position that item will be added
	 *
	 * @param item
	 *            the item to be added
	 * 
	 */
	@Override
	public void add(int pos, E item) {
		if (item == null)
			throw new NullPointerException();
		// if the list is empty, create a new node and put 𝑋 at offset 0
		Node current = new Node();
		NodeInfo info = find(pos);
		int off = info.offset;
		current = info.node;
		// establish new Node
		if (this.size == 0) {
			Node toAdd = new Node();
			toAdd.addItem(item);
			this.head.next = toAdd;
			this.tail.previous = toAdd;
			toAdd.previous = this.head;
			toAdd.next = this.tail;
			size += 1;
		}
		// if 𝑛 is the tail node and 𝑛’s predecessor has 𝑀 elements,
		// create a new node and put 𝑋 at offset 0
		else if (off == 0 && current == tail && current.previous.count == nodeSize) {
			Node newNode = new Node();
			newNode.addItem(0, item);
			tail.previous.next = newNode;
			newNode.next = tail;
			newNode.previous = tail.previous;
			tail.previous = newNode;
			size += 1;
		}
		// 𝑛 has a predecessor which has fewer than 𝑀 elements (and is not
		// the head), put 𝑋 in 𝑛’s predecessor
		else if (off == 0 && current.previous != head && current.previous.count < nodeSize) {
			current.previous.addItem(current.previous.count, item);
			size += 1;
		}

		// otherwise if there is space in node 𝑛, put 𝑋 in node 𝑛 at offset
		// off, shifting array elements as necessary
		else if (current.count < nodeSize) {
			current.addItem(off, item);
			size += 1;
		}
		// otherwise, perform a split operation: move the last 𝑀/2 elements of
		// node 𝑛 into a new successor node 𝑛′,
		// if off ≤ 𝑀/2, put 𝑋 in node 𝑛 at offset off
		else if (current.count == nodeSize) {
			Node newNode = new Node();
			current.next.previous = newNode;
			newNode.previous = current;
			newNode.next = current.next;
			current.next = newNode;
			// add elements in new Node
			for (int i = 0; i < nodeSize / 2; i++) {
				newNode.addItem(current.data[i + nodeSize / 2]);
				size += 1;
			}
			// remove elements from old Node
			for (int i = current.count - 1; i > nodeSize / 2 - 1; i--) {
				current.removeItem(i);
				size -= 1;
			}
			// if off <= 𝑀/2 put 𝑋 in node 𝑛 at offset off
			if (off <= nodeSize / 2) {
				current.addItem(off, item);
				size += 1;
			}
			// if off > 𝑀/2 put 𝑋 in node 𝑛′ at offset (off – 𝑀/2)
			else {
				newNode.addItem(off - nodeSize / 2, item);
				size += 1;
			}
		}
	}

	/**
	 * remove the item in the specified the position
	 * 
	 * @param pos
	 *            the position that item will be removed
	 */
	@Override
	public E remove(int pos) {

		Node current = new Node();
		NodeInfo info = find(pos);
		int off = info.offset;
		current = info.node;
		// if the node 𝑛 containing 𝑋 is the last node and has only one
		// element, delete it;
		if (current == tail.previous && current.count == 1) {
			E item = current.data[off];
			current.previous.next = tail;
			tail.previous = current.previous;
			size -= 1;
			current = null;
			return item;
		}
		// otherwise, if 𝑛 is the last node (thus with two or more elements) ,
		// or if 𝑛 has more than 𝑀/2 elements, remove 𝑋 from 𝑛, shifting
		// elements
		else if (current == tail.previous || current.count > nodeSize / 2) {
			E item = current.data[off];
			current.removeItem(off);
			size -= 1;
			return item;
		}
		// otherwise (the node 𝑛 must have at most 𝑀/2 elements), look at its
		// successor 𝑛′ (note that we
		// don’t look at the predecessor of 𝑛) and perform a merge operation
		else if (current.count <= nodeSize / 2 && current.next != tail) {
			// if the successor node 𝑛′ has more than 𝑀/2 elements, move the
			// first element from 𝑛′ to 𝑛. (mini-merge)
			if (current.next.count > nodeSize / 2) {
				E item = current.data[off];
				current.removeItem(off);
				size -= 1;
				current.addItem(current.next.data[0]);
				current.next.removeItem(0);
				return item;
			}
			// if the successor node 𝑛′ has 𝑀/2 or fewer elements, then move
			// all elements from 𝑛′ to 𝑛 and delete 𝑛′ (full merge)
			if (current.next.count <= nodeSize / 2) {
				E item = current.data[off];
				current.removeItem(off);
				size -= 1;
				for (int i = 0; i < current.next.count; i++) {
					current.addItem(current.count, current.next.data[i]);
				}
				current.next.next.previous = current;
				current.next = current.next.next;
				return item;
			}
		}
		return null;
	}

	/**
	 * Removes each element (from the list) that is equal to an element in an
	 * array arr[]. You should use an efficient algorithm to implement this
	 * method. One efficient implementation is given as follows. Sort the array
	 * arr[] with Arrays.sort(). Use a ListIterator object to access each
	 * element in this list. If the element is found in the array arr[] with
	 * Arrays.binarySearch(), then remove the element from the list.
	 * 
	 * @param arr
	 *            array of (unsorted) elements
	 */
	public void removeAll(E[] arr) {
		Arrays.sort(arr);
		ListIterator<E> iter = listIterator();
		while (iter.hasNext()) {
			Object elemnt = iter.next();
			if (Arrays.binarySearch(arr, elemnt) >= 0)
				iter.remove();
		}
	}

	/**
	 * 
	 * @return new ChunkyIterator object
	 */
	@Override
	public Iterator<E> iterator() {
		return new ChunkyIterator();
	}

	/**
	 * 
	 * @return new ChunkyListIterator
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new ChunkyListIterator();
	}

	/**
	 * 
	 * @param index
	 *            the logical position of cursor
	 * @return new ChunkyListIterator with the logical position of cursor
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		return new ChunkyListIterator(index);
	}

	/**
	 * Returns a string representation of this list showing the internal
	 * structure of the nodes.
	 */
	public String toStringInternal() {
		return toStringInternal(null);
	}

	/**
	 * Returns a string representation of this list showing the internal
	 * structure of the nodes and the position of the iterator.
	 *
	 * This complete example illustrates how a method in this data structure is
	 * implemented. You should study this code carefully and use this method to
	 * show the contents of the list every time you implement and use a new
	 * method.
	 *
	 * @param iter
	 *            an iterator for this list
	 */
	public String toStringInternal(ListIterator<E> iter) {
		int count = 0;
		int position = -1;
		if (iter != null) {
			position = iter.nextIndex();
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		Node current = head.next;
		while (current != tail) {
			sb.append('(');
			for (int i = 0; i < nodeSize; ++i) {
				if (i > 0)
					sb.append(", ");
				E data = current.data[i];
				if (data == null) {
					sb.append("-");
				} else {
					if (position == count) {
						sb.append("| ");
						position = -1;
					}
					sb.append(data.toString());
					++count;

					// iterator at end
					if (position == size && count == size) {
						sb.append(" |");
						position = -1;
					}
				}
			}
			sb.append(')');
			current = current.next;
			if (current != tail)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @param pos
	 *            the logical position to find
	 * @return the node and offset for the given logical index
	 */
	public NodeInfo find(int pos) {

		Node current = head.next;
		int ind = -1;
		if (pos > size) {
			throw new NullPointerException("position is out of index");
		}

		while (current != tail && ind + current.count < pos) {
			ind += current.count;
			current = current.next;
		}
		if (ind == -1) {
			ind = 0;
			return new NodeInfo(current, pos);
		} else
			return new NodeInfo(current, pos - ind - 1);
	}

	/**
	 * The class to record the node information including node and offset
	 * position
	 */
	private class NodeInfo {
		public Node node;
		public int offset;

		public NodeInfo(Node node, int offset) {
			this.node = node;
			this.offset = offset;
		}
	}

	/**
	 * Node type for this list. Each node holds a maximum of nodeSize elements
	 * in an array. Empty slots are null.
	 */
	private class Node {
		/**
		 * Array of actual data elements.
		 */
		// Unchecked warning unavoidable.
		public E[] data = (E[]) new Comparable[nodeSize];

		/**
		 * Link to next node.
		 */
		public Node next;

		/**
		 * Link to previous node;
		 */
		public Node previous;

		/**
		 * Index of the next available offset in this node, also equal to the
		 * number of elements in this node.
		 */
		public int count;

		/**
		 * Adds an item to this node at the first available offset.
		 * Precondition: count < nodeSize
		 * 
		 * @param item
		 *            element to be added
		 */
		void addItem(E item) {
			if (count >= nodeSize) {
				return;
			}
			data[count++] = item;
			// useful for debugging
			// System.out.println("Added " + item.toString() + " at index " +
			// count + " to node " + Arrays.toString(data));
		}

		/**
		 * Adds an item to this node at the indicated offset, shifting elements
		 * to the right as necessary.
		 * 
		 * Precondition: count < nodeSize
		 * 
		 * @param offset
		 *            array index at which to put the new element
		 * @param item
		 *            element to be added
		 */
		void addItem(int offset, E item) {
			if (count >= nodeSize) {
				return;
			}
			for (int i = count - 1; i >= offset; --i) {
				data[i + 1] = data[i];
			}
			++count;
			data[offset] = item;
			// useful for debugging
			// System.out.println("Added " + item.toString() + " at index " +
			// offset + " to node: " + Arrays.toString(data));
		}

		/**
		 * Deletes an element from this node at the indicated offset, shifting
		 * elements left as necessary. Precondition: 0 <= offset < count
		 * 
		 * @param offset
		 */
		void removeItem(int offset) {
			E item = data[offset];
			for (int i = offset + 1; i < nodeSize; ++i) {
				data[i - 1] = data[i];
			}
			data[count - 1] = null;
			--count;
		}
	}

	/**
	 * The class that can iterate through every element in the list via single
	 * direction
	 */
	private class ChunkyIterator implements Iterator<E> {
		/**
		 * the node the cursor is currently in
		 */
		private Node current;
		/**
		 * the logical position that cursor is currently in
		 */
		private int index;
		/**
		 * the offset that cursor is currently in
		 */
		private int off;

		/**
		 * Default constructor
		 */
		public ChunkyIterator() {
			current = head.next;
			index = 0;
			off = 0;
		}

		/**
		 * @return true if index < size, false otherwise
		 */
		@Override
		public boolean hasNext() {
			return index < size;

		}

		/**
		 * @return the next element of type E
		 */
		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			NodeInfo info = find(index);
			current = info.node;
			off = info.offset;
			if (off <= current.count - 1) {
				E item = current.data[off];
				index += 1;
				return item;
			} else {
				index += 1;
				return current.next.data[0];
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * A class that can iterate through every element in the list via double
	 * direction
	 */
	private class ChunkyListIterator implements ListIterator<E> {
		/**
		 * the node the cursor is currently in
		 */
		private Node current;
		/**
		 * the logical index position that cursor is currently in
		 */
		private int index;
		/**
		 * the logical index position that last cursor is currently in
		 */
		private int lastInd;
		/**
		 * the offset that cursor is currently in
		 */
		private int off;

		/**
		 * Default constructor
		 */
		public ChunkyListIterator() {
			this(0);

		}

		/**
		 * Constructor finds node at a given position.
		 * 
		 * @param pos
		 *            the position that the cursor is set in
		 */
		public ChunkyListIterator(int pos) {
			if (pos > size)
				throw new IndexOutOfBoundsException();
			current = head.next;
			lastInd = -1;
			off = 0;
			index = pos;
		}

		/**
		 * set the lastIndex to the initialization condition
		 */
		public void ReSet() {
			lastInd = -1;
		}

		/**
		 * @return true if index < size, false otherwise
		 */
		@Override
		public boolean hasNext() {
			return index < size;

		}

		/**
		 * @return the next element of type E
		 */
		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			NodeInfo info = find(index);
			current = info.node;
			off = info.offset;
			if (off <= current.count - 1) {
				E item = current.data[off];
				lastInd = index;
				index += 1;
				return item;
			} else {
				lastInd = index;
				index += 1;
				return current.next.data[0];
			}

		}

		/**
		 * Remove the specific item to last index cursor
		 */
		@Override
		public void remove() {
			if (lastInd == -1)
				throw new IllegalStateException();
			Node current = new Node();
			NodeInfo info = find(lastInd);
			int off = info.offset;
			current = info.node;
			// if the node 𝑛 containing 𝑋 is the last node and has only one
			// element, delete it;
			if (current == tail.previous && current.count == 1) {
				E item = current.data[off];
				current.previous.next = tail;
				tail.previous = current.previous;
				size -= 1;
				if (lastInd != index)
					index--;
				current = null;
			}
			// otherwise, if 𝑛 is the last node (thus with two or more
			// elements) ,
			// or if 𝑛 has more than 𝑀/2 elements, remove 𝑋 from 𝑛, shifting
			// elements
			else if (current == tail.previous || current.count > nodeSize / 2) {
				E item = current.data[off];
				current.removeItem(off);
				size -= 1;
				if (lastInd != index) {
					index -= 1;
				}

			}
			// otherwise (the node 𝑛 must have at most 𝑀/2 elements), look
			// at its
			// successor 𝑛′ (note that we
			// don’t look at the predecessor of 𝑛) and perform a merge
			// operation
			else if (current.count <= nodeSize / 2 && current.next != tail) {
				// if the successor node 𝑛′ has more than 𝑀/2 elements, move
				// the
				// first element from 𝑛′ to 𝑛. (mini-merge)
				if (current.next.count > nodeSize / 2) {
					E item = current.data[off];
					current.removeItem(off);
					size -= 1;
					if (lastInd != index)
						index--;
					current.addItem(current.next.data[0]);
					current.next.removeItem(0);
				}
				// if the successor node 𝑛′ has 𝑀/2 or fewer elements, then
				// move
				// all elements from 𝑛′ to 𝑛 and delete 𝑛′ (full merge)
				else if (current.next.count <= nodeSize / 2) {
					E item = current.data[off];
					current.removeItem(off);
					size -= 1;
					if (lastInd != index)
						index--;
					for (int i = 0; i < current.next.count; i++) {
						current.addItem(current.count, current.next.data[i]);
					}
					current.next.next.previous = current;
					current.next = current.next.next;
				}
			}
			ReSet();

		}

		/**
		 * Add the specific item to last index cursor
		 */
		@Override
		public void add(E item) {
			if (item == null)
				throw new NullPointerException();
			// if the list is empty, create a new node and put 𝑋 at offset 0
			Node current = new Node();
			NodeInfo info = find(index);
			int off = info.offset;
			current = info.node;

			if (size == 0) {
				Node toAdd = new Node();
				toAdd.addItem(item);
				head.next = toAdd;
				tail.previous = toAdd;
				toAdd.previous = head;
				toAdd.next = tail;
				index += 1;
				size += 1;
			}
			// if 𝑛 is the tail node and 𝑛’s predecessor has 𝑀 elements,
			// create a new node and put 𝑋 at offset 0
			else if (off == 0 && current == tail && current.previous.count == nodeSize) {
				Node newNode = new Node();
				newNode.addItem(0, item);
				// is it necessary?
				tail.previous.next = newNode;
				newNode.next = tail;
				newNode.previous = tail.previous;
				tail.previous = newNode;
				index += 1;
				size += 1;
			}
			// 𝑛 has a predecessor which has fewer than 𝑀 elements (and is not
			// the head), put 𝑋 in 𝑛’s predecessor
			else if (off == 0 && current.previous != head && current.previous.count < nodeSize) {
				current.previous.addItem(current.previous.count, item);
				index += 1;
				size += 1;
			}

			// otherwise if there is space in node 𝑛, put 𝑋 in node 𝑛 at
			// offset off, shifting array elements as necessary
			else if (current.count < nodeSize) {
				current.addItem(off, item);
				index += 1;
				size += 1;
			}
			// otherwise, perform a split operation: move the last 𝑀/2 elements
			// of node 𝑛 into a new successor node 𝑛′,
			// if off ≤ 𝑀/2, put 𝑋 in node 𝑛 at offset off
			else if (current.count == nodeSize) {
				Node newNode = new Node();
				current.next.previous = newNode;
				newNode.previous = current;
				newNode.next = current.next;
				current.next = newNode;
				// add elements in new Node
				for (int i = 0; i < nodeSize / 2; i++) {
					newNode.addItem(current.data[i + nodeSize / 2]);
					size += 1;
				}
				// remove elements from old Node
				for (int i = current.count - 1; i > nodeSize / 2 - 1; i--) {
					current.removeItem(i);
					size -= 1;
				}
				// if off <= 𝑀/2 put 𝑋 in node 𝑛 at offset off
				if (off <= nodeSize / 2) {
					current.addItem(off, item);
					index += 1;
					size += 1;
				}
				// if off > 𝑀/2 put 𝑋 in node 𝑛′ at offset (off – 𝑀/2)
				else {
					newNode.addItem(off - nodeSize / 2, item);
					index += 1;
					size += 1;
				}
			}
			ReSet();

		}

		/**
		 * @return true if it has previous item, false otherwise
		 */
		@Override
		public boolean hasPrevious() {
			return index > 0;
		}

		/**
		 * @return the next index
		 */
		@Override
		public int nextIndex() {
			return index;
		}

		/**
		 * @return the previous type E item
		 */
		@Override
		public E previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			if (current.previous == head && index == 0)
				throw new IllegalStateException();

			NodeInfo info = find(index);
			off = info.offset;
			current = info.node;
			if (off != 0) {
				lastInd = index - 1;
				index -= 1;
				return current.data[off - 1];
			} else {
				int len = current.previous.count;
				lastInd = index - 1;
				index -= 1;
				return current.previous.data[len - 1];
			}
		}

		/**
		 * @return the previous index
		 */
		@Override
		public int previousIndex() {
			return index - 1;
		}

		/**
		 * Set the item at the previous index
		 * 
		 * @param item
		 *            the specified item to be set
		 */
		@Override
		public void set(E item) {
			if (item == null)
				throw new NullPointerException();
			if (lastInd >= size)
				throw new RuntimeException("index 1");
			if (lastInd == -1)
				throw new IllegalStateException();

			NodeInfo info = find(lastInd);
			off = info.offset;
			current = info.node;
			current.data[off] = item;
			ReSet();

		}

	}

}
