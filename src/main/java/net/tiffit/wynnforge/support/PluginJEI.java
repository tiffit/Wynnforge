package net.tiffit.wynnforge.support;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import java.util.ArrayList;

@JEIPlugin
public class PluginJEI implements IModPlugin {

	public static IIngredientBlacklist blacklist;
    public static IIngredientRegistry ingredientRegistry;
	
    @Override
    public void register (IModRegistry registry) {
        blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ingredientRegistry = registry.getIngredientRegistry();
        
//        Collection<ItemStack> isIngredients = ingredientRegistry.getAllIngredients(VanillaTypes.ITEM);
//        for(ItemStack ingredient : isIngredients){
//        	if(ingredient.getItem() != Item.getItemFromBlock(Blocks.MOB_SPAWNER))blacklist.addIngredientToBlacklist(ingredient);
//        }
//        
//        Collection<FluidStack> flIngredients = ingredientRegistry.getAllIngredients(VanillaTypes.FLUID);
//        for(FluidStack ingredient : flIngredients)blacklist.addIngredientToBlacklist(ingredient);
        
       // registry.addIngredientInfo(new ItemStack(Item.getItemFromBlock(Blocks.MOB_SPAWNER)), VanillaTypes.ITEM, "A Mob Spawner.");
    }
    
	@Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    	IRecipeRegistry recipeReg = jeiRuntime.getRecipeRegistry();
    	recipeReg.hideRecipeCategory(VanillaRecipeCategoryUid.ANVIL);
    	recipeReg.hideRecipeCategory(VanillaRecipeCategoryUid.BREWING);
    	recipeReg.hideRecipeCategory(VanillaRecipeCategoryUid.CRAFTING);
    	recipeReg.hideRecipeCategory(VanillaRecipeCategoryUid.FUEL);
    	recipeReg.hideRecipeCategory(VanillaRecipeCategoryUid.SMELTING);
    	for(IIngredientType type : ingredientRegistry.getRegisteredIngredientTypes()){
    		ingredientRegistry.removeIngredientsAtRuntime(type, new ArrayList<>(ingredientRegistry.getAllIngredients(type)));
    	}
    }
	
	
    
}
