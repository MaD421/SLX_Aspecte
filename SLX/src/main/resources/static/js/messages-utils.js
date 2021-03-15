const baseURL = '/message';

const padDate = date => date.toString().padStart(2, '0');

const formatDate = date => (
    `${padDate(date.getHours())}:${padDate(date.getMinutes())} ${padDate(date.getDate())}/${padDate(date.getMonth() + 1)}/${date.getFullYear()}`
);

export {
    baseURL,
    formatDate
};