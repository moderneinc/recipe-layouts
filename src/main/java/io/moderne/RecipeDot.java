package io.moderne;

import org.apache.commons.text.WordUtils;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.config.RecipeDescriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RecipeDot {
    public static void main(String[] args) throws IOException {
        String recipeId = "org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2";
//        String recipeId = "org.openrewrite.staticanalysis.CommonStaticAnalysis";
//        String recipeId = "org.openrewrite.java.testing.junit5.JUnit4to5Migration";

        Recipe recipe = Environment.builder().scanRuntimeClasspath().build().activateRecipes(recipeId)
                .getRecipeList().get(0);

        StringBuilder g = new StringBuilder();
        g.append("digraph G {\n");
//        g.append("  ratio=\"fill\";\n");
//        g.append("  size=\"12,5!\";\n");
        g.append("  layout=sfdp;\n");
        g.append("  graph [ranksep=3, overlap=prism];\n");
        writeNodes(recipe.getDescriptor(), g, 0, 0);
        writeEdges(recipe.getDescriptor(), g);
        g.append("}");

        Path out = Paths.get("graphs");
        if (!Files.exists(out) && !out.toFile().mkdirs()) {
            throw new RuntimeException("Unable to create directory " + out);
        }

        Files.write(out.resolve(recipeId + ".dot"), g.toString().getBytes());
    }

    private static void writeNodes(RecipeDescriptor descriptor, StringBuilder g, int rank, int dupCount) {
        int nextDupCount = 0;
        List<RecipeDescriptor> recipeList = descriptor.getRecipeList();
        for (int i = 0; i < recipeList.size(); i++) {
            RecipeDescriptor next = recipeList.get(i);
            if (i < recipeList.size() - 1 &&
                next.getName().equals(recipeList.get(i + 1).getName()) &&
                next.getRecipeList().isEmpty()) {
                nextDupCount++;
                continue;
            }
            writeNodes(next, g, rank + 1, nextDupCount);
        }
        g.append("  ").append(nodeName(descriptor)).append(" [label=\"")
//                .append(descriptor.getDisplayName())
                .append(WordUtils.wrap(descriptor.getDisplayName(), 20)) //.replace("\n", "\\\n"))
                .append(dupCount == 0 ? "" : " (x" + dupCount + ")")
                .append("\",fillcolor=\"").append(rank == 0 ? "#99ffcc" : "#99aaff").append("\",style=\"filled\"];\n");
    }

    private static void writeEdges(RecipeDescriptor descriptor, StringBuilder g) {
        List<RecipeDescriptor> recipeList = descriptor.getRecipeList();
        for (int i = 0; i < recipeList.size(); i++) {
            RecipeDescriptor next = recipeList.get(i);
            if (i < recipeList.size() - 1 &&
                next.getName().equals(recipeList.get(i + 1).getName()) &&
                next.getRecipeList().isEmpty()) {
                continue;
            }
            // could we stack cards when there are many of the same type of recipe but with
            // different options in the list?
            g.append("  ").append(nodeName(descriptor))
                    .append(" -> ").append(nodeName(next)).append(" [color=\"#737a8440\"];\n");
            writeEdges(next, g);
        }
    }

    private static String nodeName(RecipeDescriptor descriptor) {
        return descriptor.getName().replace('.', '_').replace('$', '_');
    }
}
