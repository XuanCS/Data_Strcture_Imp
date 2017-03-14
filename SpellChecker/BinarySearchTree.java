//package edu.iastate.cs228.hw4;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Extension of the AbstractCollection class based on a Binary Search Tree.
 * Efficiencies may vary by implementation, but all methods should have at least
 * the worst case runtimes of a standard Tree.
 * 
 * @author Xuan Lu
 */
public class BinarySearchTree<E extends Comparable<? super E>> extends AbstractCollection<E> {

	/**
	 * Member variables to support the tree: - A Node referencing the root of
	 * the tree
	 */
	private Node<E> root;
	/**
	 * An int specifying the element count
	 */
	private int size;

	/**
	 * Constructs an empty BinarySearchTree
	 */
	public BinarySearchTree() {
		root = null;
		size = 0;
	}

	/**
	 * Constructs a new BinarySearchTree whose root is exactly the given Node.
	 * (For testing purposes, set the root to the given Node, do not clone it)
	 * 
	 * @param root
	 *            - The root of the new tree
	 * @param size
	 *            - The number of elements already contained in the new tree
	 */
	public BinarySearchTree(Node<E> root, int size) {
		this.root = root;
		this.size = size;
	}

	/**
	 * Adds the given item to the tree if it is not already there.
	 * 
	 * @return false if item already exists in the tree and true otherwise.
	 * @param item
	 *            - Item to be added to the tree
	 * @throws IllegalArgumentException
	 *             - If item is null
	 */
	@Override
	public boolean add(E item) throws IllegalArgumentException {
		if (item == null)
			throw new IllegalArgumentException();
		if (root == null) {
			root = new Node<E>(item);
			size += 1;
			return true;
		}
		Node<E> current = root;
		while (true) {
			int comp = item.compareTo(current.getData());
			if (comp == 0)
				return false;
			else if (comp > 0) {
				if (current.getRight() != null)
					current = current.getRight();
				else {
					Node<E> temp = new Node<E>(null, null, current, item);
					current.setRight(temp);
					size += 1;
					return true;
				}
			} else {// left is not null
				if (current.getLeft() != null)
					current = current.getLeft();
				else {// the case of right
					Node<E> temp = new Node<E>(null, null, current, item);
					current.setLeft(temp);
					size += 1;
					return true;
				}
			}
		}
	}

	/**
	 * Removes the given item from the tree if it is there. Because the item is
	 * an Object it will need to be cast to an E type. To verify that this is a
	 * safe cast, compare its class to the class of the root Node's data.
	 * 
	 * @return false if the list is empty or item does not exist in the tree,
	 *         true otherwise
	 * @param item
	 *            - The item to be removed from the tree
	 */
	@Override
	public boolean remove(Object item) {
		// test different case
		if (item == null || root == null || item.getClass() != root.getData().getClass())
			return false;
		E key = (E) item;
		Node<E> n = findNode(key);
		if (n == null)
			return false;
		delete(n);
		return true;
	}

	private void delete(Node<E> aNode) {
		if (aNode == null)
			throw new NullPointerException("delete");
		Node<E> toDel = aNode;
		if (toDel.getLeft() != null && toDel.getRight() != null) {
			Node<E> s = toDel.getSuccessor();
			toDel.setData(s.getData());
			toDel = s;
		}

		// at this point we know toDel has at most one child.
		if (toDel.getLeft() != null) // has left child
			linkChildToParent(toDel, toDel.getLeft());
		else // has right child or null
			linkChildToParent(toDel, toDel.getRight());
		size -= 1;
	}

	// Remove toDel by connecting its only child to its parent.
	private void linkChildToParent(Node<E> toDel, Node<E> child) {
		if (toDel == root) {
			root = child;
			if (child != null)
				child.setParent(null);
		} else {
			if (toDel.getParent().getLeft() == toDel)
				toDel.getParent().setLeft(child);
			else
				toDel.getParent().setRight(child);
			if (child != null)
				child.setParent(toDel.getParent());
		}
	} // child may be null

	/**
	 * Retrieves data of the Node in the tree that contains item. i.e. the data
	 * such that Node.data.equals(item) is true
	 * 
	 * @return null if item does not exist in the tree, otherwise the data
	 *         stored at the Node that meets the condition above.
	 * @param item
	 *            - The item to be retrieved
	 */
	public E get(E item) {
		if (findNode(item) == null)
			return null;
		Node<E> temp = findNode(item);
		return temp.getData();
	}

	/**
	 * Retrieve the root of BinarySearchTree
	 * 
	 * @return the root of this tree
	 */
	public Node<E> getRoot() {
		return root;
	}

	/**
	 * Set the root of BinarySearchTree
	 * 
	 * @param newRoot
	 *            the node<E> that will be set to root
	 */
	public void setRoot(Node<E> newRoot) {
		root = newRoot;
	}

	/**
	 * Tests whether or not item exists in the tree. i.e. this should only
	 * return true if a Node exists in the tree such that Node.data.equals(item)
	 * is true
	 * 
	 * @return false if item does not exist in the tree, otherwise true
	 * @param item
	 *            - The item check
	 */
	@Override
	public boolean contains(Object item) {
		if (item == null || size == 0 || item.getClass() != root.getData().getClass())
			return false;

		E key = (E) item;
		return findNode(key) != null;

	}

	/**
	 * findNode non recursive version according to the item
	 * 
	 * @param item
	 *            the item to search
	 * @return the last Node traversed if item is not found
	 */
	private Node<E> findNode(E item) {
		Node<E> curpos = root;
		while (curpos != null) {
			int cv = item.compareTo(curpos.getData());
			if (cv == 0)
				return curpos;
			if (cv > 0)
				curpos = curpos.getRight();
			else
				curpos = curpos.getLeft();
		}
		return null;
	}

	/**
	 * Removes all elements from the tree
	 */
	@Override
	public void clear() {
		while (root != null)
			delete(root);
	}

	/**
	 * Tests whether or not the tree contains any elements.
	 * 
	 * @return false if the tree contains at least one element, true otherwise.
	 */
	@Override
	public boolean isEmpty() {
		// TODO
		return size == 0;
	}

	/**
	 * Retrieves the number of elements in the tree.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns a new BSTIterator instance.
	 */
	@Override
	public Iterator<E> iterator() {
		return new BSTIterator();
	}

	/**
	 * Returns an ArrayList containing all elements in the tree in the order
	 * given by a preorder traversal of the tree.
	 * 
	 * @return an ArrayList of elements from the traversal.
	 */
	public ArrayList<E> getPreorderTraversal() {
		ArrayList<E> temp = new ArrayList<>();
		recPreOrderTraversal(root, temp);
		return temp;
	}

	/**
	 * 
	 * @param r
	 *            Node<E> to be added
	 * @param temp
	 *            the ArrayList<E> that Node will be add into
	 * @return the added ArrayList in preOrder Traversal
	 */
	private void recPreOrderTraversal(Node<E> r, ArrayList<E> temp) {
		if (r == null)
			return;
		temp.add(r.getData()); // "Visit the node"
		recPreOrderTraversal(r.getLeft(), temp);
		recPreOrderTraversal(r.getRight(), temp);
	}

	/**
	 * Returns an ArrayList containing all elements in the tree in the order
	 * given by a postorder traversal of the tree.
	 * 
	 * @return an ArrayList of elements from the traversal.
	 */
	public ArrayList<E> getPostOrderTraversal() {
		ArrayList<E> temp = new ArrayList<>();
		recPostOrderTraversal(root, temp);
		return temp;
	}

	/**
	 * 
	 * @param r
	 *            Node<E> to be added
	 * @param temp
	 *            the ArrayList<E> that Node will be add into
	 * @return the added ArrayList in PostOrder Traversal
	 */
	private void recPostOrderTraversal(Node<E> r, ArrayList<E> temp) {
		if (r == null)
			return;
		recPostOrderTraversal(r.getLeft(), temp);
		recPostOrderTraversal(r.getRight(), temp);
		temp.add(r.getData()); // "Visit the node"
	}

	/**
	 * Returns an ArrayList containing all elements in the tree in the order
	 * given by a inorder traversal of the tree.
	 * 
	 * @return an ArrayList of elements from the traversal.
	 */
	public ArrayList<E> getInorderTravseral() {
		ArrayList<E> temp = new ArrayList<>();
		recInOrderTraversal(root, temp);
		return temp;
	}

	/**
	 * 
	 * @param r
	 *            Node<E> to be added
	 * @param temp
	 *            the ArrayList<E> that Node will be add into
	 * @return the added ArrayList in Order Traversal
	 */
	private void recInOrderTraversal(Node<E> r, ArrayList<E> temp) {
		if (r == null)
			return;
		recInOrderTraversal(r.getLeft(), temp);
		temp.add(r.getData()); // "Visit the node"
		recInOrderTraversal(r.getRight(), temp);
	}

	/**
	 * Implementation of the Iterator interface which returns elements in the
	 * order of an inorder traversal using Nodes predecessor and successor.
	 * 
	 * @author Xuan Lu
	 */
	private class BSTIterator implements Iterator<E> {

		/**
		 * node to be visited next
		 */
		private Node<E> next;

		/**
		 * node last visited by next()
		 */
		private Node<E> last;

		public BSTIterator() {
			next = root;
			if (next != null)
				while (next.getLeft() != null)
					next = next.getLeft();
		}

		/**
		 * Returns true if more elements exist in the inorder traversal, false
		 * otherwise.
		 */
		@Override
		public boolean hasNext() {
			return next != null;
		}

		/**
		 * Returns the next item in the inorder traversal.
		 * 
		 * @return the next item in the traversal.
		 * @throws IllegalStateException
		 *             - if no more elements exist in the traversal.
		 */
		@Override
		public E next() throws IllegalStateException {
			if (next == null)
				throw new IllegalStateException();

			last = next;
			next = next.getSuccessor();
			return last.getData();
		}

		/**
		 * Removes the last item that was returned by calling next().
		 * 
		 * @throws IllegalStateException
		 *             - if next() has not been called yet or remove() is called
		 *             multiple times in a row.
		 */
		@Override
		public void remove() throws IllegalStateException {
			if (last == null)
				throw new IllegalStateException();

			if (last.getLeft() != null && last.getRight() != null)
				next = last;
			delete(last);
			last = null;
		}

	}
}
