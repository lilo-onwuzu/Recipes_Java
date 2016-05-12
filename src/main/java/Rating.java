import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Rating {
  private int id;
  private String rating;
  // rating and recipe have a one to many relationship
  private int recipe_id;

  public Rating(String rating, int recipe_id) {
    // Rating myRating.rating and myRating.recipe_id are both instance variables of the object
    this.rating = rating;
    this.recipe_id = recipe_id;
  }

  public int getId() {
   return id;
  }

  public String getRating() {
    return rating;
  }

  public int getRecipeId() {
    return recipe_id;
  }

  public static List<Rating> all(){
    // select all the columns each row in the ratings table
    String sql = "SELECT * FROM ratings";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Rating.class);
    }
  }

  @Override
  public boolean equals(Object otherRating) {
    if (!(otherRating instanceof Rating)) {
      return false;
    } else {
      Rating newRating = (Rating) otherRating;
            // if two rating object are the same, their ids must match
      return this.getId() == newRating.getId() &&
            // if two ratings are the same, their names must match
             this.getRating().equals(newRating.getRating()) &&
             // if two ratings are the same, their recipe ids must match
             this.getRecipeId() == newRating.getRecipeId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      // to save a rating object to the DB, insert the rating and recipe_id values, the id is already defined in the DB as the primary key but we still need to change the variable rating_id which is still null to match the primary key in the DB
      String sql = "INSERT INTO ratings (rating, recipe_id) VALUES (:rating, :recipe_id);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the rating_id
      this.id = (int) con.createQuery(sql, true)
        // the variables that will be strored in the DB are this.rating and this.recipe_id
        .addParameter("rating", this.rating)
        .addParameter("recipe_id", this.recipe_id)
        .executeUpdate()
        .getKey();
    }
  }

  // find(int) is static because we need to be able to return the same/static result when we use this method on all objects of Rating as well as on the class itself
  public static Rating find(int id) {
    // find the row of id = id and select all the columns in that row
    String sql = "SELECT * FROM ratings WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Rating rating = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Rating.class);
      // return that row of id=id packaged as an object
      return rating;
    }
  }

  // Rating class is not attached to a join table so you only have to delete row with id=this.id from the ratings table
  public void delete() {
    String deleteQuery = "DELETE FROM ratings WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // updateRecipeID() method is used to change the recipe attached to a rating object
  public void updateRecipeID(int recipeID) {
    String sql = "UPDATE ratings SET recipe_id=:recipe_id WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        // find the row with id equals to the variable this.id
        .addParameter("id", this.id)
        // SET the recipe_id column to the integer argument "recipeID"
        .addParameter("recipe_id", recipeID)
        .executeUpdate();
    }
  }

}
