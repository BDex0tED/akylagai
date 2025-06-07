import { useEffect, useRef, useState } from "react";
import styles from './ChatBox.module.css';

const ChatBox = ({ messages = [] }) => {
  const endRef = useRef(null);
  const [displayedMessages, setDisplayedMessages] = useState([]);

  useEffect(() => {
    setDisplayedMessages(messages);
  }, [messages]);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [displayedMessages]);

  return (
    <div className={styles.chatContainer}>
      {displayedMessages.map((msg, index) => (
        <div
          key={index}
          className={msg.role === 'user' ? styles.userMsg : styles.aiMsg}
        >
          {msg.text}
        </div>
      ))}
      <div ref={endRef} />
    </div>
  );
};

export default ChatBox;
