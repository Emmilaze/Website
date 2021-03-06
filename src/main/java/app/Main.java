package app;

import app.category.CategoryDao;
import app.comment.CommentDao;
import app.controllers.*;
import app.downloads.DownloadsDao;
import app.recipe.*;
import app.user.UserController;
import app.user.UserDao;
import app.util.*;
import app.vote.VoteDao;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;

import static app.util.OpenAPI.getOpenApiOptions;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

/**
 * Main class of the project.
 */
public class Main {

    /**
     * Object type EmailSenderTLS
     */
    public static EmailSenderTLS emailSenderTLS = new EmailSenderTLS();

    public static SentryClient sentry;

    /**
     * RecipeDao type object.
     */
    public static RecipeDao recipeDao;

    /**
     * UserDao type object.
     */
    public static UserDao userDao;

    /**
     * VoteDao type object.
     */
    public static VoteDao voteDao;

    /**
     * CategoryDao type object.
     */
    public static CategoryDao categoryDao;

    /**
     * CommentDao type object.
     */
    public static CommentDao commentDao;

    /**
     * DownloadsDao type object.
     */
    public static DownloadsDao donwloadsDao;

    /**
     * The main class. Create the objects of need types.
     * Configure the paths to the files, their controller and methods.
     */
    public static void main(String[] args) {

        Sentry.init("https://a4906a2630b745e9b056d9c525e4923d@sentry.io/1857553");
        sentry = SentryClientFactory.sentryClient();
        recipeDao = new RecipeDao();
        userDao = new UserDao();
        voteDao = new VoteDao();
        categoryDao = new CategoryDao();
        commentDao = new CommentDao();
        donwloadsDao = new DownloadsDao();

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.sessionHandler(Sessions::fileSessionHandler);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
        }).start(HerokuUtil.getHerokuAssignedPort());

        try {
            app.routes(() -> {
                get(Path.Web.INDEX, IndexController.serveIndexPage);
                get(Path.Web.LOGIN, LoginController.serveLoginPage);
                get(Path.Web.REGISTER, RegisterController.serveRegisterPage);
                get(Path.Web.POST, PostController.servePostPage);
                get(Path.Web.EDIT, PageOfEditingController.serveEditPage);
                get(Path.Web.PROFILE, ProfilesController.serveProfilesPage);
                post(Path.Web.POST, PostController.handleCreatePost);
                post(Path.Web.LOGOUT, LoginController.handleLogoutPost);
                post(Path.Web.LOGIN, LoginController.handleLoginPost);
                post(Path.Web.REGISTER, RegisterController.handleRegisterPost);
                post(Path.Web.EDIT, PageOfEditingController.handleEditPost);
                get(Path.Web.ONE_RECIPE, RecipeController.fetchOneRecipe);
                get(Path.Web.LOGOUT, LoginController.handleLogoutPost);
                get(Path.Web.BLOCK, ProfilesController.blockUserPost);
                get(Path.Web.LIKE, RecipeController.likePost);
                get(Path.Web.DISLIKE, RecipeController.dislikePost);
                get(Path.Web.DELETE_RECIPE, RecipeController.deletePost);
                get(Path.Web.SORT_BY_NEW, IndexController.newRecipes);
                get(Path.Web.SORT_BY_POPULAR, IndexController.popularRecipes);
                get(Path.Web.ABOUT, AboutPageController.serveAboutPage);
                get(Path.Web.USER_RECIPE, UserRecipesController.serveUserRecipesPage);
                get(Path.Web.UNCONFIRMED, UnconfirmedRecipesPage.serveUnconfirmedRecipesPage);
                get(Path.Web.APPROVE, RecipeController.approve);
                get(Path.Web.ADD_CATEGORY, ProfilesController.addCategory);
                post(Path.Web.ONE_RECIPE, RecipeController.makeComment);
                get(Path.Web.ACTIVATE, UserController.activateUser);
                get(Path.Web.FOLLOWED, UserRecipesController.serveLikedRecipesPage);
                get(Path.Web.CHANGE_PASSWORD, ProfilesController.changePassword);
                get(Path.Web.DOWNLOAD, RecipeController.download);
                get(Path.Web.REPORT, ReportController.serveReportPage);
                post(Path.Web.REPORT, ReportController.makeReport);
                get(Path.Web.ROBOTS, ViewUtil.serveRobotsPage);
                get(Path.Web.SEARCH, SearchPageController.serveSearchPage);
                get(Path.Web.VERSIONS, RecipeController.fetchOldRecipe);

                get(Path.Web.MESSAGE, ViewUtil.message);
                app.error(500, ViewUtil.serverError);
                app.error(404, ViewUtil.notFound);

                app.get("/", ctx -> ctx.redirect("/index/1"));

            });
        } catch (Exception e) {
            Sentry.capture(e);
        }
    }
}
