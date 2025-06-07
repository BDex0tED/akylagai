import styles from '../TextArea/TextArea.module.css';
import TextareaAutosize from 'react-textarea-autosize';

const TextArea = ({ placeholder, value, onChange , rows}) => {
  return (
    <TextareaAutosize
      className={styles.textArea}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      minRows={rows}
    />
  );
};

export default TextArea;