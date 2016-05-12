import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Ingredient {
  private String name;
  private int id;

  public Ingredient(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
   return id;
  }

  public static List<Ingredient> all(){
    String sql = "SELECT id, name FROM ingredients";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Ingredient.class);
    }
  }

  @Override
  public boolean equals(Object otherIngredient) {
    if (!(otherIngredient instanceof Ingredient)) {
      return false;
    } else {
      Ingredient newIngredient = (Ingredient) otherIngredient;
      return this.getName().equals((newIngredient.getName())) &&
             this.getId() == newIngredient.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO ingredients (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the ingredient_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Ingredient find(int id) {
    String sql = "SELECT * FROM ingredients WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Ingredient ingredient = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Ingredient.class);
      return ingredient;
    }
  }

  public void update(String update) {
    String sql = "UPDATE ingredients SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void addRecipe(Recipe myRecipe) {
    String sql = "INSERT INTO ingredients_recipes (ingredient_id, recipe_id) VALUES (:ingredient_id, :recipe_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("ingredient_id", this.getId())
        .addParameter("recipe_id", myRecipe.getId())
        .executeUpdate();
    }
  }

  public List<Recipe> getRecipes() {
    String joinQuery = "SELECT recipe_id FROM ingredients_recipes WHERE ingredient_id=:ingredientId";
    try (Connection con = DB.sql2o.open()) {
      List<Integer> recipeIds = con.createQuery(joinQuery)
        .addParameter("ingredientId", this.getId())
        .executeAndFetch(Integer.class);

      List<Recipe> recipeList = new ArrayList<Recipe>();

      for (Integer recipeId : recipeIds) {
        String taskQuery = "SELECT name FROM recipes WHERE id=:recipe_id";
          Recipe ingredient_recipe = con.createQuery(taskQuery)
            .addParameter("recipe_id", recipeId)
            .executeAndFetchFirst(Recipe.class);
            recipeList.add(ingredient_recipe);
      }
      return recipeList;
    }
  }

  public void delete() {
    String deleteQuery = "DELETE FROM ingredients WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    String joinDeleteQuery = "DELETE FROM ingredients_recipes WHERE ingredient_id=:ingredient_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("ingredient_id", this.getId())
        .executeUpdate();
    }
  }

}
