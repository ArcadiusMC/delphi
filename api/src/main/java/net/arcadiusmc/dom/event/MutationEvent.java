package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Node;

public interface MutationEvent extends Event {

  /**
   * Gets the node being removed/appended
   * @return Affected node
   */
  Node getNode();

  /**
   * Gets the index of the node that was removed/added
   * @return Mutation index
   */
  int getMutationIndex();
}
