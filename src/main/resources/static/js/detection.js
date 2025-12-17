document.addEventListener('DOMContentLoaded', function() {
    const image = document.getElementById('uploadedImage');
    const canvas = document.getElementById('detectionCanvas');

    if (!image || !canvas) return;

    const ctx = canvas.getContext('2d');
    const labels = window.vkVisionLabels || [];

    console.log('Labels:', labels);

    // Проверяем, если изображение уже загружено
    if (image.complete && image.naturalWidth > 0) {
        drawBoxes();
    } else {
        image.onload = drawBoxes;
    }

    image.onerror = function() {
        console.error('Failed to load image');
    };

    function drawBoxes() {
        console.log('Image loaded, natural width:', image.naturalWidth, 'height:', image.naturalHeight);

        // Устанавливаем размеры canvas по реальным размерам изображения
        canvas.width = image.naturalWidth;
        canvas.height = image.naturalHeight;

        labels.forEach(label => {
            if (!label?.coord || label.coord.length !== 4) {
                console.warn('Invalid label coord:', label);
                return;
            }

            const [x1, y1, x2, y2] = label.coord;
            const width = x2 - x1;
            const height = y2 - y1;

            ctx.strokeStyle = 'red';
            ctx.lineWidth = 3;
            ctx.strokeRect(x1, y1, width, height);

            ctx.fillStyle = 'red';
            ctx.font = '16px Arial';
            ctx.fillText(
                `${label.rus || label.eng || 'Object'} (${(label.prob * 100).toFixed(1)}%)`,
                x1,
                y1 - 5
            );
        });
    }
});