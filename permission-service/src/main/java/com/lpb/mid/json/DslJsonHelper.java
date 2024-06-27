package com.dslplatform.json;


import com.dslplatform.json.JsonReader;

public class DslJsonHelper {
  public static <T> void reset(JsonReader<T> reader) {
    reader.reset();
  }
}
