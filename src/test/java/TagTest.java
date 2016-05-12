import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class TagTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Tag_InstantiatesWithString_true() {
    Tag testTag = new Tag("tag");
    assertEquals("tag",testTag.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Tag.all().size());
  }

  @Test
  public void equals_returnsTrueIfTagNAMESNOTidAreTheSame_true() {
    // this.id retains 0 value until you use the save() method which gives it a non-zero id value
    Tag firstTag = new Tag("tag1");
    Tag secondTag = new Tag("tag1");
    assertTrue(firstTag.equals(secondTag));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Tag testTag = new Tag("tag");
    testTag.save();
    assertTrue(Tag.all().get(0).equals(testTag));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Tag testTag = new Tag("tag");
    testTag.save();
    Tag savedTag = Tag.all().get(0);
    assertEquals(testTag.getId(), savedTag.getId());
  }

  @Test
  public void find_findTagInDatabase_true() {
    Tag testTag = new Tag("tag");
    testTag.save();
    Tag savedTag = Tag.all().get(0);
    assertEquals(savedTag, Tag.find(testTag.getId()));
  }

  @Test
  public void update_updateTagNameInDatabase_true() {
    Tag testTag = new Tag("tag");
    testTag.save();
    testTag.update("other tag");
    assertEquals("other tag", Tag.find(testTag.getId()).getName());
  }

  @Test
  public void addRecipe_addsRecipeToTag_true() {
    Tag myTag = new Tag("Mexican");
    myTag.save();
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    myTag.addRecipe(myRecipe);
    List savedRecipes = myTag.getRecipes();
    assertEquals(1, savedRecipes.size());
  }

  @Test
  public void getRecipes_getsRecipesForATag_true() {
    Tag myTag = new Tag("Mexican");
    myTag.save();
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    myTag.addRecipe(myRecipe);
    List savedRecipes = myTag.getRecipes();
    assertEquals(1, savedRecipes.size());
  }

  @Test
  public void deleteTags_deletesTagsfromDatabase_true() {
    Tag myTag = new Tag("Mexican");
    myTag.save();
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    myTag.addRecipe(myRecipe);
    myTag.delete();
    assertEquals(0, myRecipe.getTags().size());
  }
}
