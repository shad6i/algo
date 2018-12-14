/**
 * Интерфейс, определяющий набор действий, выполняемых при обходе графа
 */
public interface Action {
    /**
     * Действие при входе в новую компоненту обхода.
     */
    void startComponent();

    /**
     * Действие при завершении обработки очередной компоненты обхода.
     */
    void finishComponent();

    /**
     * Действие при выходе из вершины в процессе обхода графа в глубину.
     * @param u Номер покидаемой вершины
     */
    void passOut(int u);
}