import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Tag {
  private int id;
  private String name;

  public Tag(String name) {
    this.name = name;
  }

  public int getId() {
   return id;
  }

  public String getName() {
    return name;
  }

  public static List<Tag> all(){
    String sql = "SELECT id, name FROM tags";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Tag.class);
    }
  }

  @Override
  public boolean equals(Object otherTag) {
    if (!(otherTag instanceof Tag)) {
      return false;
    } else {
      Tag newTag = (Tag) otherTag;
      return this.getName().equals(newTag.getName()) &&
             this.getId() == newTag.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tags (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the tag_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Tag find(int id) {
    String sql = "SELECT * FROM tags WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Tag tag = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Tag.class);
      return tag;
    }
  }

  public void update(String update) {
    String sql = "UPDATE tags SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // addRecipe() inserts tag/recipe relationship matrix using tag_id and recipe_id integers into the tags_recipes join table DB
  public void addRecipe(Recipe myRecipe) {
    // tag_id and recipe_id are the two fields in the tags_recipes join table
    // :tag_id and :recipe_id are the placeholders for the incoming values
    String sql = "INSERT INTO recipes_tags (recipe_id, tag_id) VALUES (:recipe_id, :tag_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        // the "Recipe myRecipe" argument in addRecipe() would have been already added to the list of recipes when it was created/instantiated from the Recipe class and given a id by the DB primary key. This id will be returned from myRecipe.getId()
        // addParameter("placeholder", value)
        .addParameter("recipe_id", myRecipe.getId())
        // Tag tag or "this" is the subject that we are adding an recipe to
        .addParameter("tag_id", this.getId())
        .executeUpdate();
    }
  }

  // addRecipes() makes a join table of relationships between tags and recipes/books using tag id and recipe id. To get all the recipes that have a relationship with one tag, we would need to sift through all the rows in the join table to see where the tag_id is called, export all the recipe id's attached to those rows into a list<integer> of recipe id's and then sift through the recipes table DB to collect the names recipes with those ids and adds them to an arraylist
  public List<Recipe> getRecipes() {
    String joinQuery = "SELECT recipe_id FROM recipes_tags WHERE tag_id=:tag_id";
    try(Connection con = DB.sql2o.open()) {
      List<Integer> recipeIds = con.createQuery(joinQuery)
        .addParameter("tag_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Recipe> recipeList = new ArrayList<Recipe>();

      for (Integer recipeId : recipeIds) {
        String taskQuery = "SELECT * FROM recipes WHERE id=:recipe_id";
        Recipe tag_recipe = con.createQuery(taskQuery)
          .addParameter("recipe_id", recipeId)
          .executeAndFetchFirst(Recipe.class);
        recipeList.add(tag_recipe);
      }
      return recipeList;
    }
  }

  public void delete() {
    // delete the tag from tags table
    String deleteQuery = "DELETE FROM tags WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    // then delete the tag's row in the join table as well
    String joinDeleteQuery = "DELETE FROM recipes_tags WHERE tag_id=:tag_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("tag_id", this.getId())
        .executeUpdate();
    }
  }

}
