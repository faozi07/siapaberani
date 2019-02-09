package com.siapaberani.kuis.Network;

import com.siapaberani.kuis.Model.Category;
import com.siapaberani.kuis.Model.Question;
import com.siapaberani.kuis.Model.Tutorial;

import java.util.List;

/**
 * Created by Arif Khan on 12/24/2017.
 */

public class NetworkCallMock implements ApiService {
    // Get All Categories
    @Override
    public void getCategories(String type, QueryCallback<List<Category>> callback) {

    }

    // Get Subcategories of a Category
    @Override
    public void getSubCategories(int categoryId, String type, QueryCallback<List<Category>> callback) {

    }

    // Get Questions of a Category
    @Override
    public void getQuestionsFromCategory(int categoryId, QueryCallback<List<Question>> callback) {

    }

    // Get Tutorial Content
    @Override
    public void getTutorialContent(QueryCallback<Tutorial> callback) {

    }
}
