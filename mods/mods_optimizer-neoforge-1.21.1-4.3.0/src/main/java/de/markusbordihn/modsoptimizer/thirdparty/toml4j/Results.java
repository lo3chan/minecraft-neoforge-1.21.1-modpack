/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Container;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Identifier;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Keys;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class Results {
    final Errors errors = new Errors();
    private final Set<String> tables = new HashSet<String>();
    private final Deque<Container> stack = new ArrayDeque<Container>();

    Results() {
        this.stack.push(new Container.Table(""));
    }

    void addValue(String key, Object value, AtomicInteger line) {
        Container currentTable = this.stack.peek();
        if (value instanceof Map) {
            String path = this.getInlineTablePath(key);
            if (path == null) {
                this.startTable(key, line);
            } else if (path.isEmpty()) {
                this.startTables(Identifier.from(key, null), line);
            } else {
                this.startTables(Identifier.from(path, null), line);
            }
            Map valueMap = (Map)value;
            for (Map.Entry entry : valueMap.entrySet()) {
                this.addValue((String)entry.getKey(), entry.getValue(), line);
            }
            this.stack.pop();
        } else if (currentTable.accepts(key)) {
            currentTable.put(key, value);
        } else if (currentTable.get(key) instanceof Container) {
            this.errors.keyDuplicatesTable(key, line);
        } else {
            this.errors.duplicateKey(key, line != null ? line.get() : -1);
        }
    }

    void startTableArray(Identifier identifier, AtomicInteger line) {
        String tableName = identifier.getBareName();
        while (this.stack.size() > 1) {
            this.stack.pop();
        }
        Keys.Key[] tableParts = Keys.split(tableName);
        for (int i = 0; i < tableParts.length; ++i) {
            String tablePart = tableParts[i].name;
            Container currentContainer = this.stack.peek();
            if (currentContainer.get(tablePart) instanceof Container.TableArray) {
                Container.TableArray currentTableArray = (Container.TableArray)currentContainer.get(tablePart);
                this.stack.push(currentTableArray);
                if (i == tableParts.length - 1) {
                    currentTableArray.put(tablePart, new Container.Table());
                }
                this.stack.push(currentTableArray.getCurrent());
                currentContainer = this.stack.peek();
                continue;
            }
            if (currentContainer.get(tablePart) instanceof Container.Table && i < tableParts.length - 1) {
                Container nextTable = (Container)currentContainer.get(tablePart);
                this.stack.push(nextTable);
                continue;
            }
            if (currentContainer.accepts(tablePart)) {
                Container newContainer = i == tableParts.length - 1 ? new Container.TableArray() : new Container.Table();
                this.addValue(tablePart, newContainer, line);
                this.stack.push(newContainer);
                if (!(newContainer instanceof Container.TableArray)) continue;
                this.stack.push(((Container.TableArray)newContainer).getCurrent());
                continue;
            }
            this.errors.duplicateTable(tableName, line.get());
            break;
        }
    }

    void startTables(Identifier id, AtomicInteger line) {
        String tableName = id.getBareName();
        while (this.stack.size() > 1) {
            this.stack.pop();
        }
        Keys.Key[] tableParts = Keys.split(tableName);
        for (int i = 0; i < tableParts.length; ++i) {
            String tablePart = tableParts[i].name;
            Container currentContainer = this.stack.peek();
            if (currentContainer.get(tablePart) instanceof Container) {
                Container nextTable = (Container)currentContainer.get(tablePart);
                if (i == tableParts.length - 1 && !nextTable.isImplicit()) {
                    this.errors.duplicateTable(tableName, line.get());
                    return;
                }
                this.stack.push(nextTable);
                if (!(this.stack.peek() instanceof Container.TableArray)) continue;
                this.stack.push(((Container.TableArray)this.stack.peek()).getCurrent());
                continue;
            }
            if (currentContainer.accepts(tablePart)) {
                this.startTable(tablePart, i < tableParts.length - 1, line);
                continue;
            }
            this.errors.tableDuplicatesKey(tablePart, line);
            break;
        }
    }

    Map<String, Object> consume() {
        Container values = this.stack.getLast();
        this.stack.clear();
        return ((Container.Table)values).consume();
    }

    private Container startTable(String tableName, AtomicInteger line) {
        Container.Table newTable = new Container.Table(tableName);
        this.addValue(tableName, newTable, line);
        this.stack.push(newTable);
        return newTable;
    }

    private Container startTable(String tableName, boolean implicit, AtomicInteger line) {
        Container.Table newTable = new Container.Table(tableName, implicit);
        this.addValue(tableName, newTable, line);
        this.stack.push(newTable);
        return newTable;
    }

    private String getInlineTablePath(String key) {
        Iterator<Container> descendingIterator = this.stack.descendingIterator();
        StringBuilder sb = new StringBuilder();
        while (descendingIterator.hasNext()) {
            Container next = descendingIterator.next();
            if (next instanceof Container.TableArray) {
                return null;
            }
            Container.Table table = (Container.Table)next;
            if (table.name == null) break;
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(table.name);
        }
        if (sb.length() > 0) {
            sb.append('.');
        }
        sb.append(key).insert(0, '[').append(']');
        return sb.toString();
    }

    static class Errors {
        private final StringBuilder sb = new StringBuilder();

        Errors() {
        }

        void duplicateTable(String table, int line) {
            this.sb.append("Duplicate table definition on line ").append(line).append(": [").append(table).append("]");
        }

        public void tableDuplicatesKey(String table, AtomicInteger line) {
            this.sb.append("Key already exists for table defined on line ").append(line.get()).append(": [").append(table).append("]");
        }

        public void keyDuplicatesTable(String key, AtomicInteger line) {
            this.sb.append("Table already exists for key defined on line ").append(line.get()).append(": ").append(key);
        }

        void emptyImplicitTable(String table, int line) {
            this.sb.append("Invalid table definition due to empty implicit table name: ").append(table);
        }

        void invalidTable(String table, int line) {
            this.sb.append("Invalid table definition on line ").append(line).append(": ").append(table).append("]");
        }

        void duplicateKey(String key, int line) {
            this.sb.append("Duplicate key");
            if (line > -1) {
                this.sb.append(" on line ").append(line);
            }
            this.sb.append(": ").append(key);
        }

        void invalidTextAfterIdentifier(Identifier identifier, char text, int line) {
            this.sb.append("Invalid text after key ").append(identifier.getName()).append(" on line ").append(line).append(". Make sure to terminate the value or add a comment (#).");
        }

        void invalidKey(String key, int line) {
            this.sb.append("Invalid key on line ").append(line).append(": ").append(key);
        }

        void invalidTableArray(String tableArray, int line) {
            this.sb.append("Invalid table array definition on line ").append(line).append(": ").append(tableArray);
        }

        void invalidValue(String key, String value, int line) {
            this.sb.append("Invalid value on line ").append(line).append(": ").append(key).append(" = ").append(value);
        }

        void unterminatedKey(String key, int line) {
            this.sb.append("Key is not followed by an equals sign on line ").append(line).append(": ").append(key);
        }

        void unterminated(String key, String value, int line) {
            this.sb.append("Unterminated value on line ").append(line).append(": ").append(key).append(" = ").append(value.trim());
        }

        public void heterogenous(String key, int line) {
            this.sb.append(key).append(" becomes a heterogeneous array on line ").append(line);
        }

        boolean hasErrors() {
            return this.sb.length() > 0;
        }

        public String toString() {
            return this.sb.toString();
        }

        public void add(Errors other) {
            this.sb.append((CharSequence)other.sb);
        }
    }
}

