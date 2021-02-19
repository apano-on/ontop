package it.unibz.inf.ontop.protege.gui.models;

import it.unibz.inf.ontop.protege.core.QueryManager;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class QueryManagerTreeModel implements TreeModel {
    private final QueryManager queryManager;

    public QueryManagerTreeModel(QueryManager queryManager) {
        this.queryManager = queryManager;

        queryManager.addListener(new QueryManager.EventListener() {
            @Override
            public void inserted(QueryManager.Item item, int indexInParent) {
                createEventAndNotify(item, indexInParent, TreeModelListener::treeNodesInserted);
            }

            @Override
            public void removed(QueryManager.Item item, int indexInParent) {
                createEventAndNotify(item, indexInParent, TreeModelListener::treeNodesRemoved);
            }

            @Override
            public void changed(QueryManager.Item item, int indexInParent) {
                createEventAndNotify(item, indexInParent, TreeModelListener::treeNodesChanged);
            }
        });
    }

    @Override
    public Object getRoot() {
        return queryManager.getRoot();
    }

    @Override
    public Object getChild(Object parentO, int index) {
        QueryManager.Item parent = (QueryManager.Item)parentO;
        return index < parent.getChildNumber() ? parent.getChild(index) :  null;
    }

    @Override
    public int getChildCount(Object parent) {
        return ((QueryManager.Item)parent).getChildNumber();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((QueryManager.Item)node).getChildNumber() == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
// TODO:
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((QueryManager.Item)parent).getChildIndex((QueryManager.Item)child);
    }

    private final ArrayList<TreeModelListener> listeners = new ArrayList<>();

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        if (listener != null && !listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }

    private void createEventAndNotify(QueryManager.Item item, int indexInParent, BiConsumer<TreeModelListener, TreeModelEvent> eventConsumer) {
        List<QueryManager.Item> path = new LinkedList<>();
        for (QueryManager.Item c = item.getParent(); c != null; c = c.getParent())
            path.add(0, c); // the path starts from the root

        TreeModelEvent event = new TreeModelEvent(this, new TreePath(path.toArray()),
                new int[]{ indexInParent }, new Object[]{ item });

        listeners.forEach(l -> eventConsumer.accept(l, event));
    }
}
