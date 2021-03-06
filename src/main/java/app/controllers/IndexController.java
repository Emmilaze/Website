package app.controllers;

import app.util.Path;
import app.util.ViewUtil;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;

import java.util.Map;

import static app.Main.recipeDao;
import static app.util.RequestUtil.*;
import static app.util.ViewUtil.*;

/**
 * Class controller of Main page.
 */
public class IndexController {

    /**
     * Serve Main page with recipes from the Data Base.
     */
    @OpenApi(
            path = "/index/{pageId}",
            method = HttpMethod.GET,
            summary = "Returns a list of recipes",
            description = "Main page",
            tags = "Cook Eat Repeat",
            responses = {
                    @OpenApiResponse(status = "500", description = "The error is not with you, but with us on the server. We apologize.",
                            content = @OpenApiContent(type = "application/json", from = ViewUtil.class)),
                    @OpenApiResponse(status = "404", description = "Nothing has matched your criterias.")
            }
    )
    public final static Handler serveIndexPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        recipeDao.seachedList = null;
        int present = Integer.parseInt(getParamId(ctx));
        if (isMore(present)) {
            ctx.redirect("/index/1");
        }
        if (recipeDao.sort)
            model.put("recipes", recipeDao.getPopular(present));
        else
            model.put("recipes", recipeDao.getAllRecipes(present));
        model.put("previous", getPreviousPage(present));
        model.put("next", getNextPage(present));
        ctx.render(Path.Template.INDEX, model);
    };

    /**
     * Method-post that call function to sort the current recipes by popularity.
     */
    @OpenApi(
            path = "/index/{pageId}",
            method = HttpMethod.GET,
            summary = "Returns a list of recipes by popular filter",
            description = "Main page",
            tags = "Cook Eat Repeat",
            responses = {
                    @OpenApiResponse(status = "500", description = "The error is not with you, but with us on the server. We apologize.",
                            content = @OpenApiContent(type = "application/json", from = ViewUtil.class)),
                    @OpenApiResponse(status = "404", description = "Nothing has matched your criterias.")
            }
    )
    public final static Handler popularRecipes = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        int present = Integer.parseInt(getParamId(ctx));
        recipeDao.sort = true;
        model.put("recipes", recipeDao.getPopular(present));
        model.put("present", present);
        model.put("previous", getPreviousPage(present));
        model.put("next", getNextPage(present));
        ctx.render(Path.Template.INDEX, model);
    };

    /**
     * Method-post that call function to sort the current recipes by new.
     */
    @OpenApi(
            path = "/index/{pageId}",
            method = HttpMethod.GET,
            summary = "Returns a list of recipes with filter by new",
            description = "Main page",
            tags = "Cook Eat Repeat",
            responses = {
                    @OpenApiResponse(status = "500", description = "The error is not with you, but with us on the server. We apologize.",
                            content = @OpenApiContent(type = "application/json", from = ViewUtil.class)),
                    @OpenApiResponse(status = "404", description = "Nothing has matched your criterias.")
            }
    )
    public final static Handler newRecipes = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        int present = Integer.parseInt(getParamId(ctx));
        recipeDao.sort = false;
        model.put("recipes", recipeDao.getNewest(present));
        model.put("previous", getPreviousPage(present));
        model.put("next", getNextPage(present));
        ctx.render(Path.Template.INDEX, model);
    };
}
