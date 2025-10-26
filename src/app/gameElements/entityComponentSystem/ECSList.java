
package app.gameElements.entityComponentSystem;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public final class ECSList {
	private int componentCount;
	private final HashMap<Integer, Integer> entityIdToComponentIndexLookup;
	private final List<Integer> entityIdWithComponentList;
	private final List<Object> componentList;
	
	public ECSList() {
		componentCount = 0;
		entityIdToComponentIndexLookup = new HashMap<>();
		entityIdWithComponentList = new ArrayList<>();
		componentList = new ArrayList<>();
	}
	
	public int getElementCount() { return componentCount; }
	public List<Object> getElementList() { return componentList; }
	
	public void addElement(int entityId, Object newComponent) {
		entityIdToComponentIndexLookup.put(entityId, componentCount++);
		componentList.add(newComponent);
		entityIdWithComponentList.add(entityId);
	}
	public void removeElement(int entityId) {
		int index = entityIdToComponentIndexLookup.get(entityId);
		int indexOfLast = componentCount - 1;
		
		int entityIdAtIndexOfLast = entityIdWithComponentList.get(indexOfLast);
		
		Object componentAtIndexOfLast = componentList.get(indexOfLast);
		
		// set component at remove index to the component at the end of the list
		componentList.set(index, componentAtIndexOfLast);
		entityIdWithComponentList.set(index, entityIdAtIndexOfLast);
		
		// remove the last element to prevent loitering
		componentList.remove(indexOfLast);
		entityIdWithComponentList.remove(indexOfLast);
		
		// update lookup hash map
		entityIdToComponentIndexLookup.remove(entityId);
		entityIdToComponentIndexLookup.put(entityIdAtIndexOfLast, index);
		
		componentCount--;
	}
	
	public void printEntityIdWithComponentList() {
		System.out.print("Entity Identifiers: { ");
		for (int i = 0; i < componentCount; i++) {
			 System.out.print(entityIdWithComponentList.get(i));
			 if (i < componentCount - 1) {
				 System.out.println(", ");
			 }
		}
		System.out.println(" }");
	}
}
