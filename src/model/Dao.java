package model;

import java.util.List;
import java.util.Map;

public interface Dao <T>{
    Map<Integer,T> getAll();
}
