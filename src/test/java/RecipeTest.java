import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipeTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Recipe_InstantiatesWithString_true() {
    Recipe testRecipe = new Recipe("recipe");
    assertEquals("recipe",testRecipe.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Recipe.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Recipe firstRecipe = new Recipe("recipe1");
    Recipe secondRecipe = new Recipe("recipe1");
    assertTrue(firstRecipe.equals(secondRecipe));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Recipe testRecipe = new Recipe("recipe");
    testRecipe.save();
    assertTrue(Recipe.all().get(0).equals(testRecipe));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Recipe testRecipe = new Recipe("recipe");
    testRecipe.save();
    Recipe savedRecipe = Recipe.all().get(0);
    assertEquals(testRecipe.getId(), savedRecipe.getId());
  }

  @Test
  public void find_findRecipeInDatabase_true() {
    Recipe testRecipe = new Recipe("recipe");
    testRecipe.save();
    Recipe savedRecipe = Recipe.all().get(0);
    assertEquals(savedRecipe, Recipe.find(testRecipe.getId()));
  }

  @Test
  public void updateName_updateRecipeNameInDatabase_true() {
    Recipe testRecipe = new Recipe("recipe");
    testRecipe.save();
    testRecipe.updateName("other recipe");
    assertEquals("other recipe", Recipe.find(testRecipe.getId()).getName());
  }

  @Test
  public void updateInstructions_updateRecipeInstructionsInDatabase_true() {
    Recipe testRecipe = new Recipe("recipe");
    testRecipe.save();
    testRecipe.updateInstructions("other recipe");
    assertEquals("other recipe", Recipe.find(testRecipe.getId()).getInstructions());
  }

  @Test
  public void getRatings_retrievesAllRatingsFromDatabase_List() {
    Recipe myRecipe = new Recipe("recipe");
    myRecipe.save();
    Rating firstRating = new Rating("rating",myRecipe.getId());
    firstRating.save();
    Rating secondRating = new Rating("rating",myRecipe.getId());
    secondRating.save();
    // make an array of rating objects
    Rating[] allRatings = new Rating[] {firstRating, secondRating};
    assertTrue(myRecipe.getRatings().containsAll(Arrays.asList(allRatings)));
  }

  @Test
  public void addTag_addsTagToRecipe_true() {
    Recipe myRecipe = new Recipe("Rice");
    // adds Book to list of books
    myRecipe.save();
    Tag myTag = new Tag("Mexican");
    // adds Tag to list of tags
    myTag.save();
    // create relationship between tag and book
    myRecipe.addTag(myTag);
    Tag savedTag = myRecipe.getTags().get(0);
    assertTrue(myTag.equals(savedTag));
  }

  @Test
  public void getTags_getsTagForARecipe_true() {
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    Tag myTag = new Tag("Mexican");
    myTag.save();
    myRecipe.addTag(myTag);
    List savedTags = myRecipe.getTags();
    assertEquals(1, savedTags.size());
  }

  @Test
  public void addIngredient_addsIngredientToRecipe_true() {
    Recipe myRecipe = new Recipe("Rice");
    // adds Book to list of books
    myRecipe.save();
    Ingredient myIngredient = new Ingredient("Mexican");
    // adds Tag to list of tags
    myIngredient.save();
    // create relationship between tag and book
    myRecipe.addIngredient(myIngredient);
    Ingredient savedIngredient = myRecipe.getIngredients().get(0);
    assertTrue(myIngredient.equals(savedIngredient));
  }

  @Test
  public void getIngredient_getsIngredientsForARecipe_true() {
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    Ingredient myIngredient = new Ingredient("Mexican");
    myIngredient.save();
    myRecipe.addIngredient(myIngredient);
    List<Ingredient> savedIngredients = myRecipe.getIngredients();
    assertEquals(1, savedIngredients.size());
  }

  @Test
  public void delete_deletesAllRecipesFromRecipesTableAndAssociationsTables() {
    Tag myTag = new Tag("Mexican");
    myTag.save();
    Recipe myRecipe = new Recipe("Rice");
    myRecipe.save();
    myRecipe.addTag(myTag);
    myRecipe.delete();
    assertEquals(0, myTag.getRecipes().size());
  }

}
