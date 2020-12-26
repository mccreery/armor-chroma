package nukeduck.armorchroma;

/** Permet de récupérer la valeur d'un attribut avant qu'elle soit relimitée
 * (pour {@link nukeduck.armorchroma.mixin.EntityAttributeInstanceMixin EntityAttributeInstanceMixin}) */
public interface UnclampedEntityAttribute {

    public double getUnclampedValue();

}
