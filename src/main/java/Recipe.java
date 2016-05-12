import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

// Recipe class has 2 many to many relationships with Tag and Ingredient and one one to many relationship with rating
// need methods: addTag(), getTags(), add Ingredient(), getIngredients(), getRatings().
public class Recipe {
  // this id for recipe will not be assigned in the construct but will be returned as this.id when we save() or insert the object instance into the DB. Until then it will have a value of "null"
  private int id;
  private String name;
  private String instructions = "";

  public Recipe(String name) {
    // the property/attribute/instance variable (this.name) of the Recipe object instance or "this" will be assigned to the argument String name once constructed
    this.name = name;
    this.instructions = instructions;
  }

  public int getId() {
   return id;
  }

  public String getName() {
    return name;
  }

  public String getInstructions() {
    return instructions;
  }

  // all() will collect the static list of all the rows or objects in the DB. The DB class is defined in DB.java file and references the library DB
  public static List<Recipe> all(){
    String sql = "SELECT * FROM recipes";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Recipe.class);
    }
  }

  @Override
  // .equals() methods to compare both the names and ids of two Recipe objects and return a boolean if they are the same
  public boolean equals(Object otherRecipe) {
    if (!(otherRecipe instanceof Recipe)) {
      return false;
    } else {
      Recipe newRecipe = (Recipe) otherRecipe;
      // should return true if the string names and integer ids are equal
      return this.getName().equals(newRecipe.getName()) &&
             this.getId() == newRecipe.getId() &&
             this.getInstructions().equals(newRecipe.getInstructions());
    }
  }

  // save() will insert rows of recipe objects (id, name) to the recipes table
  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes (name, instructions) VALUES (:name, :instructions);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the recipe_id. The recipe object instance id is now set/assigned
      this.id = (int) con.createQuery(sql, true)
        // the value to be saved is the name of the recipe object that is being saved (this.name)
        .addParameter("name", this.name)
        .addParameter("instructions", this.instructions)
        .executeUpdate()
        .getKey();
    }
  }

  // find(id) uses an integer id in the argument to select a row or a recipe object instance from the recipes table and return the object. Because the recipe's id was set using the primary key field of the recipes table, they should still match if we are correctly cleaning up or DBs after use
  public static Recipe find(int id) {
    String sql = "SELECT * FROM recipes WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Recipe recipe = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Recipe.class);
      return recipe;
    }
  }

  // update() finds the row or object instance with id=this.id (recipe's id) and resets its name to String update
  public void updateName(String update) {
    String sql = "UPDATE recipes SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // update() finds the row or object instance with id=this.id (recipe's id) and resets its instructions to String newInstructions
  public void updateInstructions(String newInstructions) {
    String sql = "UPDATE recipes SET instructions=:instructions WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("instructions", newInstructions)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // getRatings() method for ratings/recipes one to many relationship
  public List<Rating> getRatings() {
    String sql = "SELECT * FROM ratings where recipe_id=:recipe_id";
    try (Connection con = DB.sql2o.open()) {
      return con.createQuery(sql)
        .addParameter("recipe_id", this.id)
        .executeAndFetch(Rating.class);
    }
  }

  // addTag() inserts recipe/tag relationship matrix using tag_id and recipe_id integers into the recipes_tags join table DB
  public void addTag(Tag myTag) {
    // tag_id and recipe_id are the two fields in the recipes_tags join table
    // :tag_id and :recipe_id are the placeholders for the incoming values
    String sql = "INSERT INTO recipes_tags (tag_id, recipe_id) VALUES (:tag_id, :recipe_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        // the "Tag myTag" argument in addTag() would have been already added to the list of tags when it was created/instantiated from the Tag class and given a id by the DB primary key. This id will be returned from myTag.getId()
        // addParameter("placeholder", value)
        .addParameter("tag_id", myTag.getId())
        // Recipe recipe or "this" is the subject that we are adding an tag to
        .addParameter("recipe_id", this.getId())
        .executeUpdate();
    }
  }

  // addTags() makes a join table of relationships between tag and books using tag id and recipe id. To get all the tags that have a relationship with one book, we would need to sift through all the rows in the join table to see where the recipe_id is called, export all the tag id's attached to those rows into a list<integer> of tag id's and then sift through the tags table DB to collect the names tags with those ids and adds them to an arraylist
  public List<Tag> getTags() {
    String joinQuery = "SELECT tag_id FROM recipes_tags WHERE recipe_id=:recipe_id";
    try (Connection con = DB.sql2o.open()) {
      List<Integer> tagIds = con.createQuery(joinQuery)
        .addParameter("recipe_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Tag> tagList = new ArrayList<Tag>();

      for (Integer tagId : tagIds) {
        String taskQuery = "SELECT * FROM tags WHERE id=:tag_id";
          Tag recipe_tag = con.createQuery(taskQuery)
            .addParameter("tag_id", tagId)
            .executeAndFetchFirst(Tag.class);
            tagList.add(recipe_tag);
      }
      return tagList;
    }
  }

  public void addIngredient(Ingredient myIngredient) {
    String sql = "INSERT INTO ingredients_recipes (ingredient_id, recipe_id) VALUES (:ingredient_id, :recipe_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("ingredient_id", myIngredient.getId())
        .addParameter("recipe_id", this.getId())
        .executeUpdate();
    }
  }

  public List<Ingredient> getIngredients() {
    String joinQuery = "SELECT ingredient_id FROM ingredients_recipes WHERE recipe_id=:recipe_id";
    try (Connection con = DB.sql2o.open()) {
      List<Integer> ingredientIds = con.createQuery(joinQuery)
        .addParameter("recipe_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Ingredient> ingredientList = new ArrayList<Ingredient>();

      for (Integer ingredientId : ingredientIds) {
        String taskQuery = "SELECT * FROM ingredients WHERE id=:ingredient_id";
          Ingredient ingredient_recipe = con.createQuery(taskQuery)
            .addParameter("ingredient_id", ingredientId)
            .executeAndFetchFirst(Ingredient.class);
            ingredientList.add(ingredient_recipe);
      }
      return ingredientList;
    }
  }

  // Recipe myRecipe.delete() for instance finds the row where id equals this.id OR myRecipe.id and deletes it
  public void delete() {
    // delete the recipe from recipes table
    String deleteQuery = "DELETE FROM recipes WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    // then delete the recipe's row in the recipes_tags join table
    String joinDelete1Query = "DELETE FROM recipes_tags WHERE recipe_id=:recipe_id";
      con.createQuery(joinDelete1Query)
        .addParameter("recipe_id", this.getId())
        .executeUpdate();
    // then delete the recipe's row in the ingredients_recipes join table
    String joinDelete2Query = "DELETE FROM ingredients_recipes WHERE recipe_id=:recipe_id";
      con.createQuery(joinDelete2Query)
        .addParameter("recipe_id", this.getId())
        .executeUpdate();
    }
  }

}
