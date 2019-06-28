package ru.anatol.sjema.printer;

import ru.anatol.sjema.producer.model.tree.TreeAny;
import ru.anatol.sjema.producer.model.tree.TreeModel;
import ru.anatol.sjema.producer.model.tree.TreeNode;
import ru.anatol.sjema.producer.model.tree.TreeSchema;
import ru.anatol.sjema.producer.model.tree.TreeType;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

public class TreeModelPrinter {

    private TreeModel treeModel;

    //---------------------------------------------------------------------------

    public TreeModelPrinter(TreeModel treeModel) {
        this.treeModel = treeModel;
    }

    //---------------------------------------------------------------------------

    public String print() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        print(printStream, 0);
        return outputStream.toString();
    }

    //---------------------------------------------------------------------------

    public void print(PrintStream printStream, int indent) {
        ObjectPrinter modelPrinter = new ObjectPrinter();

        if (treeModel.getTargetNamespace() != null) {
            modelPrinter.put("targetNamespace", treeModel.getTargetNamespace());
        }

        if (treeModel.getNamespaces() != null) {
            ObjectPrinter namespacesPrinter = new ObjectPrinter();
            modelPrinter.put("namespaces", namespacesPrinter);
            for (Map.Entry<String, String> entry : treeModel.getNamespaces().entrySet()) {
                namespacesPrinter.put(entry.getKey(), entry.getValue());
            }
        }

        if (treeModel.getSchemas() != null) {
            ArrayPrinter importsPrinter = new ArrayPrinter();
            modelPrinter.put("schemas", importsPrinter);
            for (TreeSchema treeSchema : treeModel.getSchemas()) {
                importsPrinter.put(toSchemaPrinter(treeSchema));
            }
        }

        if (treeModel.getNodes() != null && !treeModel.getNodes().isEmpty()) {
            ArrayPrinter elementsPrinter = new ArrayPrinter();
            modelPrinter.put("nodes", elementsPrinter);
            for (TreeNode treeNode : treeModel.getNodes()) {
                elementsPrinter.put(toNodePrinter(treeNode));
            }
        }

        modelPrinter.print(printStream, indent);
    }

    private Printer toSchemaPrinter(TreeSchema treeSchema) {
        ObjectPrinter schemaPrinter = new ObjectPrinter();
        schemaPrinter.put("name", treeSchema.getName());
        schemaPrinter.put("hash", treeSchema.getHash());
        schemaPrinter.put("mode", treeSchema.getMode().name());
        schemaPrinter.put("targetNamespace", treeSchema.getTargetNamespace());
        return schemaPrinter;
    }

    private Printer toNodePrinter(TreeNode treeNode) {
        ObjectPrinter nodePrinter = new ObjectPrinter();
        nodePrinter.put("id", treeNode.getIdentifier().getName());
        nodePrinter.put("name", treeNode.getName());
        nodePrinter.put("namespace", treeNode.getNamespace());
        nodePrinter.put("mode", treeNode.getMode().toString());
        if (treeNode.getAnnotations() != null && !treeNode.getAnnotations().isEmpty()) {
            nodePrinter.put("annotations", new ArrayPrinter(treeNode.getAnnotations()));
        }
        if (treeNode.getType() != null) {
            nodePrinter.put("type", toTypePrinter(treeNode.getType()));
        }
        nodePrinter.put("path", treeNode.getPath());
        if (treeNode.getAny() != null) {
            nodePrinter.put("any", toAnyPrinter(treeNode.getAny()));
        }
        if (treeNode.getNodes() != null && !treeNode.getNodes().isEmpty()) {
            ArrayPrinter nodesPrinter = new ArrayPrinter();
            nodePrinter.put("nodes", nodesPrinter);
            for (TreeNode subTreeNode : treeNode.getNodes()) {
                nodesPrinter.put(toNodePrinter(subTreeNode));
            }
        }
        return nodePrinter;
    }

    private Printer toTypePrinter(TreeType treeType) {
        ObjectPrinter typePrinter = new ObjectPrinter();
        if (treeType.getIdentifier() != null) {
            typePrinter.put("id", treeType.getIdentifier().getName());
        }
        typePrinter.put("name", treeType.getName());
        typePrinter.put("namespace", treeType.getNamespace());
        typePrinter.put("mode", treeType.getMode().toString());
        if (treeType.getAnnotations() != null && !treeType.getAnnotations().isEmpty()) {
            typePrinter.put("annotations", new ArrayPrinter(treeType.getAnnotations()));
        }

        return typePrinter;
    }

    private Printer toAnyPrinter(TreeAny treeAny) {
        ObjectPrinter anyPrinter = new ObjectPrinter();
        anyPrinter.put("processContents", treeAny.getProcessContents());
        if (treeAny.getNamespaces() != null && !treeAny.getNamespaces().isEmpty()) {
            anyPrinter.put("namespaces", new ArrayPrinter(treeAny.getNamespaces()));
        }
        return anyPrinter;
    }

}
