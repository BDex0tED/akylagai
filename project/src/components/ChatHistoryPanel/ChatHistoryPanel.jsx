import styles from "./ChatHistoryPanel.module.css";

const ChatHistoryPanel = ({ sessions, onSelect, activeSessionId }) => {
  return (
    <div className={styles.panel}>
      <h3>Баарлашуулардын тизмеси</h3>
      <ul>
        {sessions.map((s) => (
          <li
            key={s.id}
            onClick={() => onSelect(s.id)}
            className={`${styles.sessionItem} ${activeSessionId === s.id ? styles.active : ""}`}
          >
            {s.title || `Баарлашуу #${s.id}`}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatHistoryPanel;