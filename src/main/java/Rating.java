import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Rating {
  private int id;
  private String rating;
  private int recipe_id;

  public Rating(String rating, int recipe_id) {
    this.rating = rating;
    this.recipe_id = recipe_id;
  }

  public String getRating() {
    return rating;
  }

  public int getId() {
   return id;
  }

  public int getRecipeId() {
    return recipe_id;
  }

  public static List<Rating> all(){
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
      return this.getRating().equals(newRating.getRating()) &&
             this.getId() == newRating.getId() &&
             this.getRecipeId() == newRating.getRecipeId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO ratings (rating, recipe_id) VALUES (:rating, :recipe_id);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the rating_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("rating", this.rating)
        .addParameter("recipe_id", recipe_id)
        .executeUpdate()
        .getKey();
    }
  }

  public static Rating find(int id) {
    String sql = "SELECT * FROM ratings WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Rating rating = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Rating.class);
      return rating;
    }
  }

  public void updateID(int newId) {
    String sql = "UPDATE ratings SET rating=:rating WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("rating", newId)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void updateRecipeID(int recipeID) {
    String sql = "UPDATE ratings SET recipe_id=:recipe_id WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("recipe_id", recipeID)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void delete() {
    String deleteQuery = "DELETE FROM ratings WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

}
