package edu.bu.cs622.jlitebox.options;

import java.io.Serializable;
import java.util.Optional;

public interface ObjectPersistence<T extends Serializable> {
    Optional<T> load();

    boolean save(T obj);
}
