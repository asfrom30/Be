package com.doyoon.android.bravenewworld.domain.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DOYOON on 7/8/2017.
 */

public class FirebaseHelper {

    public static final String TAG = FirebaseHelper.class.getSimpleName();

    public static HashMap<String, HashMap<String, String>> dbStructureMap;

    private static String stackToPath(Stack<String> nodeStack){
        String[] nodes = new String[nodeStack.size()];
        nodeStack.toArray(nodes);

        String path = "";
        for(int i = 0; i < nodes.length; i++) {
            path += nodes[i] + "/";
        }
        return path;
    }

    private static String stackToPathWithoutKey(Stack<String> nodeStack) {
        String[] nodes = new String[nodeStack.size()];
        nodeStack.toArray(nodes);

        String path = "";
        for(int i = 0; i < nodes.length - 1; i++) {
            path += nodes[i] + "/";
        }
        return path;
    }

    private static void buildParentNodeStack(Stack<String> parentNodeStack, String currentNodeName, int preDepth, int currentDepth) {
        if (preDepth > currentDepth) {       // depth가 낮아질때는...
            for(int i=0; i < preDepth - currentDepth + 1; i++) {
                parentNodeStack.pop();
            }
        } else if (preDepth == currentDepth) {
            parentNodeStack.pop();
        }
        parentNodeStack.add(currentNodeName);
    }

    private static void putAllAttiributeToDbStructureMap(String nodeName, XmlPullParser parser, String dbpath){
        /* preapare hash map */
        HashMap<String, String> attributeMap;
        if(dbStructureMap.containsKey(nodeName)){
            attributeMap = dbStructureMap.get(nodeName);
            if(attributeMap == null){
                attributeMap = new HashMap<>();
                dbStructureMap.put(nodeName, attributeMap);
            }
        } else {
            attributeMap = new HashMap<>();
            dbStructureMap.put(nodeName, attributeMap);
        }

        /* insert attribute and value */
        for(int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(null, attrName);
            attributeMap.put(attrName, attrValue);
        }
        // todo // FIXME: 7/9/2017 hird wiring
        attributeMap.put("modelDir", dbpath);
    }

    public static void buildDbStructure(Context context){

        int preDepth = 0;
        Stack<String> modelPathStack = new Stack<>();
        dbStructureMap = new HashMap<>();
        try {
            InputStream inputStream = context.getAssets().open("database_structure.xml");
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(inputStream, null);

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)  {

                switch (event){
                    case XmlPullParser.START_TAG:

                        /* Current TAG info */
                        String currentTagName = myParser.getName();
                        int currentDepth = myParser.getDepth();
                        String currentType = myParser.getAttributeValue(null, "type");

                        // todo // FIXME: 7/9/2017 hard wiring
                        if("primary-key".equals(currentType)){
                            buildParentNodeStack(modelPathStack, "{" + currentTagName + "}", preDepth, currentDepth);
                        } else {
                            buildParentNodeStack(modelPathStack, currentTagName, preDepth, currentDepth);
                        }
                        preDepth = currentDepth;
                        putAllAttiributeToDbStructureMap(currentTagName, myParser, stackToPathWithoutKey(modelPathStack));
                        // Log.e(currentTagName, "TAG NAME = [" + currentTagName + "], Pre is [" + preDepth +  "], Depth is [" + currentDepth + "], Path is [" + stackToPathWithoutKey(modelPathStack) + "]");
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = myParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getModelDir(String modelName, String... accessKeys) {

        HashMap<String, String> modelAttributeMap = getModelAttribute(modelName);
        String modelDir = modelAttributeMap.get("modelDir");

        if (modelDir == null) {
            Log.e(TAG, "Can not read Model Path, Model Dir is null");
            return null;
        }

        List<String> needParamList = FirebaseHelper.findNeedParams(modelDir);
        if (accessKeys.length < needParamList.size()) {
            Log.e(TAG, "Model Dir : " + modelDir);
            // Log.e( null -> need this value attribute refer="";
            throw new NullPointerException("Not enough params, if you want to access this Model, you need more param(key)..");
        }

        boolean accessKeyNullFlag = false;

        for(int i =0; i <needParamList.size(); i ++) {
            if (accessKeys[i] == null) {
                accessKeyNullFlag = true;
                continue;
            }
            modelDir = modelDir.replace(needParamList.get(i), accessKeys[i]);
        }

        if (accessKeyNullFlag) {
            Log.e(TAG, "One of the AccessKeys null, Can' not return model dir");
            return null;
        }

        return modelDir;
    }
    // Model Path = Model Dir + Model Key..
    public static String getModelPath(String modelName, String... accessKeys) {
        String modelDir = getModelDir(modelName, accessKeys);
        if (modelDir == null) {
            return null;
        }
        // todo need validate check

        return modelDir + modelName + "/";
    }

    public static void printAllModelKey(){
        for (Map.Entry<String, HashMap<String, String>> entry : dbStructureMap.entrySet()) {
            Log.e("Model Name is ",  entry.getKey());
            HashMap<String, String> hashMap = entry.getValue();
            for (Map.Entry<String, String> entry1 : hashMap.entrySet()) {
                Log.e("key/value ", "        " + entry1.getKey() + " / " + entry1.getValue());
            }
        }
    }

    public static <T extends FirebaseModel> String toMakeModelKey(String modelName, T t, String... accessKeys) {

        HashMap<String, String> modelAttributeMap = getModelAttribute(modelName);

        boolean isBundle = Boolean.parseBoolean(modelAttributeMap.get("isBundle")); // // FIXME: 7/9/2017 hird wiring
        boolean isAutoGenerateModelKey = Boolean.parseBoolean(modelAttributeMap.get("isAutoGenerateModelKey"));


        if(!isBundle){
            return modelName;
        }

        String modelKey = "";

        if(isAutoGenerateModelKey){
            String modelDir = getModelDir(modelName, accessKeys);
            modelKey = FirebaseDatabase.getInstance().getReference(modelDir).push().getKey();
        } else {
            if (t.getKey() == null) {
                throw new NullPointerException( "[" + t.getClass().getSimpleName() + "] Model key is null, If isAutoGenerateKey attribute is false, you have to define your own model key.");
            }
            modelKey = t.getKey();
        }

        return modelKey;
    }
    public static String getModelName(Object obj){
        return obj.getClass().getSimpleName().toLowerCase();
    }

    public static HashMap<String, String> getModelAttribute(String modelName){
        HashMap<String, String> modelAttributeMap = dbStructureMap.get(modelName);
        return modelAttributeMap;
    }

    private static List<String> findNeedParams(String modelDir){
        String pattern = "(\\{\\w+\\})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(modelDir);
        List<String> paramList = new ArrayList<>();
        while(m.find()){
            paramList.add(m.group(1));
        }
        return paramList;
    }

    private static void printInsertLog(String modelName, boolean isBundle, boolean isAutoGenerateModelKey, String modelDir, String modelPath){
        Log.e(TAG, "==========================Fire Base Helper =======================");
        Log.e(TAG, "model name = [" + modelName + "], is bundle = [" + isBundle + "], isAutoGenerateKey = [" + isAutoGenerateModelKey + "]");
        Log.e(TAG, "model directory = " + modelDir);
        Log.e(TAG, "model path = " + modelPath);
        Log.e(TAG, "                              ");
    }

}
