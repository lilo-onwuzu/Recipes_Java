import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {

  @Override
  protected void before() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/recipe_test", null, null);
  }

  @Override
  protected void after() {
    try(Connection con = DB.sql2o.open()) {
      String deleteTagQuery = "DELETE FROM tags *;";
      String deleteRatingQuery = "DELETE FROM ratings *;";
      String deleteRecipeQuery = "DELETE FROM recipes *;";
      String deleteIngredientQuery = "DELETE FROM ingredients *;";
      con.createQuery(deleteTagQuery).executeUpdate();
      con.createQuery(deleteRatingQuery).executeUpdate();
      con.createQuery(deleteRecipeQuery).executeUpdate();
      con.createQuery(deleteIngredientQuery).executeUpdate();
    }
  }
}
