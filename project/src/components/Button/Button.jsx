import styles from '../Button/Button.module.css';

const Button = ({ text, onClick, type = "button"}) => {
  return (
    <div>
      <button className={styles.Button} type={type} onClick={onClick}>{text}</button>
    </div>
  );
};

export default Button;