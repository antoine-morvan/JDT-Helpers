# JDT-Helpers

JDT Visitors to help analyse some specific characteristics

https://antoine-morvan.github.io/JDT-Helpers/

## Installation

In Eclipse, go to "Help > Install New Software ...", then add a new update site:
   * name : JDT-Helpers
   * URL : https://antoine-morvan.github.io/JDT-Helpers/update-site/

Then select the feature and follow instructions. Restart Eclipse after installation is done.

## Looking up for non generated methods

### Description

This feature add an entry in the context menu of the JDT package explorer:

![Context Menu Example](context_menu.png)

The result of the analysis is shown in the console in textual format. It lists only Java classes found at the root of the compilation units that have at least one non generated method. Typical result is as follows:

```
Non generated methods found in given selected Java elements
  type >> Refinement.java :
    .getFilePath()
    .setFilePath()
  type >> PiGraph.java :
    .getVertexNamed()
    .getFifoIded()
    .getHierarchicalActorFromPath()
    .getParameterNamedWithParent()
    .getAllVertices()
  type >> AbstractActor.java :
    .getPath()
  type >> Actor.java :
    .isHierarchical()
    .getGraph()
  type >> AbstractVertex.java :
    .getPortNamed()
  type >> FunctionPrototype.java :
    .format()
```
