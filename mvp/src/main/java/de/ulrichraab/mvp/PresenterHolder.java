package de.ulrichraab.mvp;


import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;


/**
 * Holder for {@link Presenter} objects.
 * @author Ulrich Raab
 */
public class PresenterHolder {

   private static final Logger LOG = Logger.getLogger(PresenterHolder.class.getName());

   private HashMap<UUID, Presenter> presenters;

   /**
    * Creates a new {@link PresenterHolder} instance.
    */
   public PresenterHolder () {
      presenters = new HashMap<>();
   }

   /**
    * Adds the specified presenter to the holder. Generates a new id for the presenter and returns this id.
    * @param presenter The presenter to add.
    * @return The id with which the specified presenter is associated
    */
   public UUID add (Presenter presenter) {
      UUID id;
      do {
         id = UUID.randomUUID();
      }
      while (contains(id));
      put(id, presenter);
      return id;
   }

   /**
    * Associates the specified presenter with the specified id. If the holder previously contained a mapping for the id,
    * the old presenter is replaced.
    * @param id The id with which the specified presenter is to be associated.
    * @param presenter The presenter to be associated with the specified id.
    */
   public void put (UUID id, Presenter presenter) {
      presenters.put(id, presenter);
   }

   /**
    * Returns the presenter to which the specified id is mapped, or null if this holder contains no mapping for the id.
    * @param id The id whose associated presenter is to be returned.
    * @param <P> The type of the presenter.
    * @return The presenter to which the specified id is mapped, or null if this holder contains no mapping for the id.
    */
   public <P extends Presenter> P get (UUID id) {
      Presenter presenter = presenters.get(id);
      if (presenter == null) {
         LOG.info("Presenter with id " + id + " not found");
         return null;
      }
      try {
         // noinspection unchecked
         return (P) presenter;
      } catch (ClassCastException e) {
         LOG.info(e.getMessage());
         return null;
      }
   }

   /**
    * Removes the presenter for the specified id from this holder if present.
    * @param id The id whose presenter is to be removed.
    */
   public void remove (UUID id) {
      presenters.remove(id);
   }

   /**
    * Returns true if this holder contains a presenter for the specified id.
    * @param id The id whose presence in this holder is to be checked.
    * @return {@code true} if this holder contains a presenter for the specified id, {@code false} otherwise.
    */
   public boolean contains (UUID id) {
      if (id == null) {
         return false;
      }
      return presenters.containsKey(id);
   }
}
