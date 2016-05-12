import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;

public class IngredientTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Ingredient_InstantiatesWithString_String() {
    Ingredient testIngredient = new Ingredient("ingredient");
    assertEquals("ingredient",testIngredient.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Ingredient.all().size());
  }

  @Test
  public void equals_returnsTrueIfIngredientsAreTheSame_true() {
    Ingredient firstIngredient = new Ingredient("ingredient1");
    Ingredient secondIngredient = new Ingredient("ingredient1");
    assertTrue(firstIngredient.equals(secondIngredient));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Ingredient testIngredient = new Ingredient("ingredient");
    testIngredient.save();
    assertTrue(Ingredient.all().get(0).equals(testIngredient));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Ingredient testIngredient = new Ingredient("ingredient");
    testIngredient.save();
    Ingredient savedIngredient = Ingredient.all().get(0);
    assertEquals(testIngredient.getId(), savedIngredient.getId());
  }

  @Test
  public void find_findIngredientInDatabase_true() {
    Ingredient testIngredient = new Ingredient("ingredient");
    testIngredient.save();
    Ingredient savedIngredient = Ingredient.all().get(0);
    assertEquals(savedIngredient, Ingredient.find(testIngredient.getId()));
  }

  @Test
  public void update_updateIngredientNameInDatabase_true() {
    Ingredient testIngredient = new Ingredient("ingredient");
    testIngredient.save();
    testIngredient.update("other ingredient");
    assertEquals("other ingredient", Ingredient.find(testIngredient.getId()).getName());
  }

  @Test
  public void addRecipe_addsRecipeToIngredient_true() {
    Ingredient myIngredient = new Ingredient("ingredient");
    myIngredient.save();
    Recipe myRecipe = new Recipe("recipe");
    myRecipe.save();
    myIngredient.addRecipe(myRecipe);
    Recipe savedRecipe = myIngredient.getRecipes().get(0);
    assertTrue(myRecipe.equals(savedRecipe));
  }

  @Test
  public void getRecipes_getsIngredientForARecipe_true() {
    Ingredient myIngredient = new Ingredient("ingredient");
    myIngredient.save();
    Recipe myRecipe = new Recipe("recipe");
    myRecipe.save();
    myIngredient.addRecipe(myRecipe);
    List savedRecipes = myIngredient.getRecipes();
    assertEquals(1, savedRecipes.size());
  }

  @Test
  public void delete_deletesAllRecipesAndIngredientsAssociations() {
    Ingredient myIngredient = new Ingredient("ingredient");
    myIngredient.save();
    Recipe myRecipe = new Recipe("recipe");
    myRecipe.save();
    myIngredient.addRecipe(myRecipe);
    myIngredient.delete();
    assertEquals(0, myIngredient.getRecipes().size());
  }
}
