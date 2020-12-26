package nukeduck.armorchroma;

/** Gets the unclamped value of an attribute (for use with
 * {@link nukeduck.armorchroma.mixin.EntityAttributeInstanceMixin EntityAttributeInstanceMixin}) */
public interface UnclampedEntityAttribute {

    public double getUnclampedValue();

}
