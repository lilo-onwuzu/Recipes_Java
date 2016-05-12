import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;

public class RatingTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Rating_InstantiatesWithString_true() {
    Rating testRating = new Rating("rating", 2);
    assertEquals("rating",testRating.getRating());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Rating.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Rating firstRating = new Rating("rating", 2);
    Rating secondRating = new Rating("rating", 2);
    assertTrue(firstRating.equals(secondRating));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Rating testRating = new Rating("rating", 2);
    testRating.save();
    assertTrue(Rating.all().get(0).equals(testRating));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Rating testRating = new Rating("rating", 2);
    testRating.save();
    Rating savedRating = Rating.all().get(0);
    assertEquals(testRating.getId(), savedRating.getId());
  }

  @Test
  public void find_findRatingInDatabase_true() {
    Rating testRating = new Rating("rating", 2);
    testRating.save();
    Rating savedRating = Rating.all().get(0);
    assertEquals(savedRating, Rating.find(testRating.getId()));
  }

  @Test
  public void delete_deletesAllRatings() {
    Rating myRating = new Rating("rating", 2);
    myRating.save();
    myRating.delete();
    assertEquals(0, myRating.all().size());
  }

  @Test
  public void updateID_updateRecipeDescriptionInDatabase_true() {
    Rating testRating = new Rating("rating", 2);
    testRating.save();
    testRating.updateID(4);
    assertEquals(4, Rating.find(testRating.getId()).getRating());
  }

  @Test
  public void updateRecipeID_updateTaskDescriptionInDatabase_true() {
    Rating testRating = new Rating("rating", 2);
    testRating.save();
    testRating.updateRecipeID(4);
    assertEquals(4, Rating.find(testRating.getId()).getRecipeId());
  }

}
