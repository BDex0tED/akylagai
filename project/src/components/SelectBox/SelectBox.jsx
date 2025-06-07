import styles from '../SelectBox/SelectBox.module.css';

const SelectBox = ({ options = [], value, onChange, label }) => {

  const hasOptgroups =
    options.length > 0 && options[0].subcategories && Array.isArray(options[0].subcategories);

  return (
    <div>
      {label && (
        <label>
          <p className={styles.p}>{label}</p>
        </label>
      )}

      <select
        className={styles.Select}
        value={value}
        onChange={(e) => onChange(e.target.value)}
      >
        {!hasOptgroups &&
     
          options.map((option, idx) => {
           
            if (typeof option === "string") {
              return (
                <option key={idx} value={option}>
                  {option}
                </option>
              );
            }
            if (typeof option === "object" && option !== null) {
              return (
                <option key={idx} value={option.value ?? option.label}>
                  {option.label}
                </option>
              );
            }
            return null;
          })}

        {hasOptgroups &&
        
          options.map((category, i) =>
            category.subcategories.map((sub, j) => (
              <optgroup key={`${i}-${j}`} label={`${category.label} / ${sub.label}`}>
                {sub.options.map((item, k) => (
                  <option
                    key={`${i}-${j}-${k}`}
                    value={`${category.label}::${sub.label}::${item}`}
                  >
                    {item}
                  </option>
                ))}
              </optgroup>
            ))
          )}
      </select>
    </div>
  );
};

export default SelectBox;