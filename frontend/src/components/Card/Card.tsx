import styles from "./Card.module.css";
import * as React from "react";

type CardProps = {
    title?: string;
    children: React.ReactNode;
};

const Card = ({ title, children }: CardProps) => {
    return (
        <div className={styles.card}>
            {title && <h2 className={styles.title}>{title}</h2>}
            <div className={styles.content}>{children}</div>
        </div>
    );
};

export default Card;
