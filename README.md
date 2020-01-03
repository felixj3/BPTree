# B+ Tree
Independent project that I worked on during winter break <br>
<br>
In a B+ Tree, pointers to data records (values) are only stored in leaf nodes. Leaf nodes are linked to provide a linear traversal of data. Internal nodes provide additional indexing. Given the difference in functionality of internal/leaf nodes, separate objects are created for each typs of node. However, they are all subclasses of the abstract class Node. <br>
<br>
I implemented the methods (insert, get, rangeSearch) of the B+ Tree. I did not implement the delete method. For the node classes, I implemented all of their methods. <br>
<br>
I also messed around with generic methods in order to make my life easier when changing the type of my keys or values. Check out this link for more information on a B+ Tree: [https://www.javatpoint.com/b-plus-tree](https://www.javatpoint.com/b-plus-tree)
