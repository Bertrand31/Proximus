package damysos

case class Damysos(
  private val latitudeGeoTrie: Node = Node(),
  private val longitudeGeoTrie: Node = Node()
) {

  private val DefaultPrecision = 6

  def toList(): List[PointOfInterst] = latitudeGeoTrie.toList

  // We arbitrarily use the latitudeGeoTrie for this operation, but either would be fine
  def contains(point: PointOfInterst): Boolean =
    latitudeGeoTrie.findLeaf(point.coordinates.latitudePath) match {
      case Some(leaf: Leaf) => leaf.locations contains point
      case _                => false
    }

  def findSurrounding(
    coordinates: Coordinates,
    precision: Int = DefaultPrecision
  ): List[PointOfInterst] = {
    val partialLatitudePath = coordinates.latitudePath.take(precision)
    val latitudeMatches = this.latitudeGeoTrie.findLeaf(partialLatitudePath) match {
      case Some(node: Node) => node.toList
      case _                => List()
    }
    val partialLongitudePath = coordinates.longitudePath.take(precision)
    val longitudeMatches = this.longitudeGeoTrie.findLeaf(partialLongitudePath) match {
      case Some(node: Node) => node.toList
      case _                => List()
    }
    latitudeMatches intersect longitudeMatches
  }

  def :+(item: PointOfInterst): Damysos =
    this.copy(
      latitudeGeoTrie = (this.latitudeGeoTrie.insertAtPath(item, item.coordinates.latitudePath)),
      longitudeGeoTrie = (this.longitudeGeoTrie.insertAtPath(item, item.coordinates.longitudePath))
    )

  // TraversableOnce encompasses both normal collections and Iterator. So this one method can be
  // used either with a normal collection or a lazy one, like reading from a file line by line.
  def ++(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ :+ _)
}
